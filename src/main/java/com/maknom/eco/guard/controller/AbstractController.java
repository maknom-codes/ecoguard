package com.maknom.eco.guard.controller;
import com.maknom.eco.guard.model.geom.GeoJsonFeature;
import com.maknom.eco.guard.model.geom.PointGeometry;
import com.maknom.eco.guard.model.incident.IncidentBean;
import com.maknom.eco.guard.model.user.UserBean;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import reactor.core.publisher.FluxSink;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractController {

   public String getCookieValue(HttpServletRequest request, String cookieName) {
      if (request.getCookies() == null) {
         return null;
      }

      return Arrays.stream(request.getCookies())
              .filter(cookie -> cookieName.equals(cookie.getName()))
              .map(Cookie::getValue)
              .findFirst()
              .orElse(null);
   }

   private ResponseCookie buildHttpOnlyCookie(String key, String token, int duration) {
      return ResponseCookie.from(key, token)
              .httpOnly(true)
              .secure(false)
              .path("/")
              .maxAge(Duration.ofMinutes(duration))
              .sameSite("strict")
              .domain("localhost")
              .build();
   }

   public void publishSyncEvent(SyncEvent event, Map<String, FluxSink<SyncEvent>> syncPublishers) {
      syncPublishers.values().forEach(sink -> sink.next(event));
   }

   public GeoJsonFeature buildSyncIncidentData(IncidentBean incident, UserBean userBean) {

      PointGeometry geometry = new PointGeometry(incident.getGeom().getX(), incident.getGeom().getY());
      Map<String, Object> properties = new HashMap<>();
      properties.put("id", userBean.getName());
      properties.put("zoneId", incident.getZoneId());
      properties.put("userId", incident.getUserId());
      properties.put("category", incident.getCategory());
      properties.put("description", incident.getDescription());
      properties.put("urgency", incident.getUrgency());
      properties.put("reportDate", incident.getReportDate().toString());

      return new GeoJsonFeature(geometry, properties);
   }

}
