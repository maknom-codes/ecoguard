package com.maknom.eco.guard.integration;


import com.maknom.eco.guard.PostgresContainerConfig;
import com.maknom.eco.guard.model.incident.IncidentBean;
import com.maknom.eco.guard.model.incident.IncidentRequest;
import com.maknom.eco.guard.model.incident.IncidentService;
import com.maknom.eco.guard.model.incident.UrgencyType;
import com.maknom.eco.guard.repository.ProtectedZoneRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Transactional
@Sql(scripts = "/init-db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class IncidentIntegrationTest extends PostgresContainerConfig {

   @Autowired
   private IncidentService incidentService;

   @Autowired
   private ProtectedZoneRepository zoneRepository;



   @Test
   void whenIncidentIsReported_thenItShouldBeLinkedToCorrectZone() {

      IncidentRequest input = new IncidentRequest();
      input.setLatitude(4.0809);
      input.setLongitude(9.7743);
      input.setCategory("DEFORESTATION");
      input.setDescription("Fire ");
      input.setUrgency(UrgencyType.MEDIUM.toString());

      IncidentBean incidentBean = incidentService.create("koko.popom@gmail.com", input);

      assertNotNull(incidentBean.getZoneId());
      assertEquals("Reserve Akwa", zoneRepository.findById(incidentBean.getZoneId()).get().getName());
   }

   @Test
   void shouldFindIncidentsInGeoJsonFormat() {

      List<IncidentBean> results = incidentService.getAllIncidents();

      assertFalse(results.isEmpty());
      Assertions.assertNotNull(results.get(0).getGeom());
      assertTrue(results.get(0).getGeom().getGeometryType().contains("Point"));
      Assertions.assertEquals(results.get(0).getGeom().getCoordinate().x, 9.75);
   }
}
