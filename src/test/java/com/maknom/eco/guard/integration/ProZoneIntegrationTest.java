package com.maknom.eco.guard.integration;

import com.maknom.eco.guard.PostgresContainerConfig;
import com.maknom.eco.guard.model.geom.GeoJsonFeature;
import com.maknom.eco.guard.model.geom.PolygonGeometry;
import com.maknom.eco.guard.model.zone.ProtectedZoneBean;
import com.maknom.eco.guard.model.zone.ProtectedZoneRequest;
import com.maknom.eco.guard.model.zone.ProtectedZoneService;
import com.maknom.eco.guard.repository.ProtectedZoneRepository;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Polygon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@Transactional
@Sql(scripts = "/init-db.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class ProZoneIntegrationTest extends PostgresContainerConfig {

   @Autowired
   private ProtectedZoneService protectedZoneService;

   @Autowired
   private ProtectedZoneRepository zoneRepository;



   @Test
   void whenProZoneIsCreated_thenItShouldSuccess() {

      List<List<Double>> coordinates = new ArrayList<>();
      coordinates.add(List.of(7.77, 4.77));
      coordinates.add(List.of(7.87, 4.78));
      coordinates.add(List.of(7.77, 4.87));
      coordinates.add(List.of(8.77, 4.77));
      ProtectedZoneRequest zoneRequest = new ProtectedZoneRequest();
      zoneRequest.setName("Reserve de Bakossa");
      zoneRequest.setDataCoordinates(coordinates);

      GeoJsonFeature feature = protectedZoneService.create(zoneRequest);

      assertNotNull(feature.getProperties());
      assertNotNull(feature.getProperties().get("id"));
      assertEquals("Reserve de Bakossa", feature.getProperties().get("name"));

      PolygonGeometry geometry = (PolygonGeometry) feature.getGeometry();
      assertNotNull(geometry);
      assertEquals("Polygon", geometry.getType());
      double[][][] coords = geometry.getCoordinates();
      assertEquals(7.77, geometry.getCoordinates()[0][0][0], "Longitude of first point is incorrect");
      assertEquals(4.77, geometry.getCoordinates()[0][0][1], "Latitude of first point is incorrect");

      assertEquals(5, coords[0].length, "Polygon should have 5 points: auto closure");

      assertEquals(7.77, geometry.getCoordinates()[0][4][0]);
      assertEquals(4.77, geometry.getCoordinates()[0][4][1]);
   }
}
