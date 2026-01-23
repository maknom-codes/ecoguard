package com.maknom.eco.guard.services;

import com.maknom.eco.guard.model.incident.IncidentBean;
import com.maknom.eco.guard.model.incident.IncidentRequest;
import com.maknom.eco.guard.model.incident.IncidentService;
import com.maknom.eco.guard.model.incident.SyncDataRequest;
import com.maknom.eco.guard.model.incident.UrgencyType;
import com.maknom.eco.guard.model.user.UserBean;
import com.maknom.eco.guard.model.zone.ProtectedZoneBean;
import com.maknom.eco.guard.repository.IncidentRepository;
import com.maknom.eco.guard.repository.ProtectedZoneRepository;
import com.maknom.eco.guard.repository.UserRepository;
import com.maknom.eco.guard.service.IncidentServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Point;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;


@ExtendWith(MockitoExtension.class)
public class IncidentServiceTest {

   @Mock
   private IncidentRepository incidentRepository;

   @Mock
   private ProtectedZoneRepository protectedZoneRepository;

   @Mock
   private UserRepository userRepository;

   private IncidentService incidentService;

   private IncidentBean sampleIncident;

   private UserBean sampleUser;

   private ProtectedZoneBean sampleZone;

   private IncidentRequest incidentRequest;

   private SyncDataRequest sampleSyncRequest;

   @BeforeEach
   void setup() {
      incidentService = new IncidentServiceImpl(protectedZoneRepository, userRepository, incidentRepository);
      sampleIncident = new IncidentBean();
      sampleIncident.setId(1L);
      sampleIncident.setCategory("FIRE");
      sampleIncident.setUrgency(UrgencyType.CRITICAL);

      sampleZone = new ProtectedZoneBean();
      sampleZone.setId(1L);
      sampleZone.setName("Marecages de Logbaba");

      sampleUser = new UserBean();
      sampleUser.setId(1L);
      sampleUser.setEmail("koko.popom@gmail.com");
      sampleUser.setName("Koko Popom");

      incidentRequest = new IncidentRequest();
      incidentRequest.setCategory("FIRE");
      incidentRequest.setUrgency(UrgencyType.CRITICAL.toString());
      incidentRequest.setDescription("description");
      incidentRequest.setLongitude(9.77430248260498);
      incidentRequest.setLatitude(4.080981254577637);

      sampleSyncRequest = new SyncDataRequest();
      sampleSyncRequest.setId(1L);
      sampleSyncRequest.setIncidentRequest(incidentRequest);
      sampleSyncRequest.setTimestamp(LocalDateTime.now().toString());
   }

   @Test
   void createIncident_ShouldThrowException_WhenIncidentIsOverAProtectedZone() {
      Exception exception = assertThrows(RuntimeException.class, () -> {
         incidentService.create(sampleUser.getEmail(), incidentRequest);
      });
      assertEquals("Incident is over a protected zone", exception.getMessage());
      Mockito.verify(incidentRepository, never()).save(Mockito.any());
   }


   @Test
   void createIncident_ShouldReturnSavedIncident() {

      Mockito.when(incidentRepository.save(Mockito.any(IncidentBean.class))).thenReturn(sampleIncident);
      Mockito.when(protectedZoneRepository.findZoneContainingPoint(any(Point.class)))
              .thenReturn(Optional.of(sampleZone));
      Mockito.when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(sampleUser));

      IncidentBean incidentBean = incidentService.create(sampleUser.getEmail(), incidentRequest);
      Assertions.assertNotNull(incidentBean);
      assertEquals(UrgencyType.CRITICAL, incidentBean.getUrgency());
      Mockito.verify(incidentRepository, Mockito.times(1)).save(Mockito.any(IncidentBean.class));
   }

   @Test
   void createIncident_ShouldReturnSavedIncidentWithSyncRequest() {

      Mockito.when(incidentRepository.save(Mockito.any(IncidentBean.class))).thenReturn(sampleIncident);
      Mockito.when(protectedZoneRepository.findZoneContainingPoint(any(Point.class)))
              .thenReturn(Optional.of(sampleZone));
      Mockito.when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(sampleUser));

      IncidentBean incidentBean = incidentService.create(sampleUser.getEmail(), sampleSyncRequest);
      Assertions.assertNotNull(incidentBean);
      assertEquals(UrgencyType.CRITICAL, incidentBean.getUrgency());
      assertEquals(1L, incidentBean.getId());
      Mockito.verify(incidentRepository, Mockito.times(1)).save(Mockito.any(IncidentBean.class));
   }

   @Test
   void getAllIncident_ShouldReturnIncidents() {
      Mockito.when(incidentRepository.findAllIncidents()).thenReturn(List.of(sampleIncident));
      List<IncidentBean> incidentBeans = incidentService.getAllIncidents();
      assertFalse(incidentBeans.isEmpty());
      assertTrue(incidentBeans.contains(sampleIncident));
      assertEquals(incidentBeans.size(), 1);
   }
}
