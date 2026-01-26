package com.maknom.eco.guard.controller;
import com.maknom.eco.guard.model.geom.GeoJsonFeature;
import com.maknom.eco.guard.model.geom.GeoJsonFeatureCollection;
import com.maknom.eco.guard.model.geom.GeoJsonService;
import com.maknom.eco.guard.model.incident.IncidentBean;
import com.maknom.eco.guard.model.incident.IncidentRequest;
import com.maknom.eco.guard.model.incident.IncidentService;
import com.maknom.eco.guard.model.incident.SyncDataRequest;
import com.maknom.eco.guard.model.user.UserBean;
import com.maknom.eco.guard.model.user.UserRequest;
import com.maknom.eco.guard.model.user.UserService;
import com.maknom.eco.guard.model.zone.ProtectedZone;
import com.maknom.eco.guard.model.zone.ProtectedZoneBean;
import com.maknom.eco.guard.model.zone.ProtectedZoneRequest;
import com.maknom.eco.guard.model.zone.ProtectedZoneService;
import com.maknom.eco.guard.service.AuthenticationService;
import graphql.GraphQLContext;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.ContextValue;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Controller
public class EcoGuardController extends AbstractController {

   private static final String ACCESS_KEY_TOKEN = "access_token";
   private static final String REFRESH_KEY_TOKEN = "refresh_token";
   private static final Logger log = LoggerFactory.getLogger(EcoGuardController.class);
   private final Map<String, FluxSink<SyncEvent>> syncPublishers = new ConcurrentHashMap<>();

   private final GeoJsonService geoJsonService;

   private final UserService userService;
   private final ProtectedZoneService protectedZoneService;
   private final AuthenticationService authenticationService;
   private final IncidentService incidentService;




   public EcoGuardController(UserService userService,
                             ProtectedZoneService protectedZoneService,
                             AuthenticationService authenticationService,
                             IncidentService incidentService,
                             GeoJsonService geoJsonService) {
      this.protectedZoneService = protectedZoneService;
      this.incidentService = incidentService;
      this.authenticationService = authenticationService;
      this.geoJsonService = geoJsonService;
      this.userService = userService;
   }


   @MutationMapping
   public AuthResponse register(@Argument UserRequest userRequest, GraphQLContext context) {
      UserBean userBean = userService.create(userRequest);
      String accessToken = authenticationService.generateAccessToken(userBean);
      String refreshToken = authenticationService.generateRefreshToken(userBean);
      context.put(ACCESS_KEY_TOKEN, accessToken);
      context.put(REFRESH_KEY_TOKEN, refreshToken);
      return new AuthResponse(
              "Login Success",
              true,
              new UserResponse((
                      userBean.getName()),
                      userBean.getRole().toString(),
                      userBean.getId(),
                      userBean.getUsername())
      );
   }


   @QueryMapping(name = "getZones")
   @PreAuthorize("isAuthenticated()")
   public EcoGuardResponse get() {
      GeoJsonFeatureCollection protectedZones = protectedZoneService.getAllZones();
      List<IncidentBean> incidents = incidentService.getAllIncidents();
      GeoJsonFeatureCollection incidentZones = geoJsonService.convertIncidentsToGeoJson(incidents);
      return EcoGuardResponse.builder()
              .incidentZones(incidentZones)
              .protectedZones(protectedZones)
              .build();
   }


   @MutationMapping(name = "createProtectedZone")
   @PreAuthorize("hasRole('ADMIN')")
   public ProtectedZone createZone(@Argument(name = "input") ProtectedZoneRequest protectedZoneRequest) {
      if (protectedZoneRequest.getName().length() < 3) {
         throw new IllegalArgumentException("Name is too short");
      }
      GeoJsonFeature feature = protectedZoneService.create(protectedZoneRequest);
      ProtectedZone zone = new ProtectedZone();
      zone.setId((Long) feature.getProperties().get("id"));
      return zone;
   }


   @MutationMapping
   @PreAuthorize("isAuthenticated()")
   public IncidentBean createIncident(@Argument(name = "input") IncidentRequest incidentRequest,
                                      Authentication authentication) {
      IncidentBean incidentBean = incidentService.create(authentication.getName(), incidentRequest);
      UserBean userBean = userService.getUserById(incidentBean.getUserId()).get();
      SyncEvent syncEvent = SyncEvent.builder()
              .timestamp(LocalDateTime.now().toString())
              .type(SyncEventType.INCIDENT.toString())
              .data(buildSyncIncidentData(incidentBean, userBean))
              .build();
      publishSyncEvent(syncEvent, syncPublishers);
      return incidentBean;
   }

   @MutationMapping
   public SyncResponse syncOfflineData(
           @Argument("items") List<SyncDataRequest> items,
           Authentication authentication,
           @ContextValue("X-Sync-Source") String syncSource) {

      if (!"EcoGuard-Offline-Agent".equals(syncSource)) {
         throw new IllegalStateException("Accès refusé : La synchronisation doit provenir du Service Worker.");
      }
      if (authentication == null || !authentication.isAuthenticated()) {
         throw new RuntimeException("Authentication required for sync");
      }

      log.info("Sync request from {}: {} items",
              syncSource != null ? syncSource : "unknown",
              items.size());
      List<Long> syncedIds = new ArrayList<>();
      List<Long> failedIds = new ArrayList<>();

      for (SyncDataRequest item : items) {
         try {
            IncidentBean incidentBean = incidentService.create(authentication.getName(), item);
            UserBean userBean = userService.getUserById(incidentBean.getUserId()).get();
            syncedIds.add(item.getId());
            SyncEvent syncEvent = SyncEvent.builder()
                    .timestamp(LocalDateTime.now().toString())
                    .type(SyncEventType.INCIDENT.toString())
                    .data(buildSyncIncidentData(incidentBean, userBean))
                    .build();
            publishSyncEvent(syncEvent, syncPublishers);
         } catch (Exception e) {
            log.error("Failed to sync item {}: {}", item.getId(), e.getMessage());
            failedIds.add(item.getId());
         }
      }
      return new SyncResponse(failedIds.isEmpty(), syncedIds, failedIds, String.format("Synced %d/%d items",
              syncedIds.size(), items.size()));
   }

   @SubscriptionMapping
   public Publisher<SyncEvent> syncCompleted() {
      return Flux.create(sink -> {
         String clientId = UUID.randomUUID().toString();
         syncPublishers.put(clientId, sink);

         sink.onCancel(() -> syncPublishers.remove(clientId));
         sink.onDispose(() -> syncPublishers.remove(clientId));
      }, FluxSink.OverflowStrategy.LATEST);
   }
}
