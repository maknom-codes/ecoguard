package com.maknom.eco.guard.services;

import com.maknom.eco.guard.model.geom.GeoJsonFeature;
import com.maknom.eco.guard.model.geom.GeoJsonService;
import com.maknom.eco.guard.model.geom.PolygonGeometry;
import com.maknom.eco.guard.model.zone.ProtectedZoneBean;
import com.maknom.eco.guard.model.zone.ProtectedZoneRequest;
import com.maknom.eco.guard.model.zone.ProtectedZoneService;
import com.maknom.eco.guard.repository.ProtectedZoneRepository;
import com.maknom.eco.guard.service.ProtectedZoneServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Polygon;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ZoneServiceTest {

   @Mock
   private ProtectedZoneRepository protectedZoneRepository;

   @Mock
   private GeoJsonService geoJsonService;

   private ProtectedZoneService zoneService;

   private ProtectedZoneBean sampleZone;

   private ProtectedZoneRequest zoneRequest;

   @Captor
   private ArgumentCaptor<ProtectedZoneBean> zoneBeanArgumentCaptor;

   @BeforeEach
   void setup() {
      zoneService = new ProtectedZoneServiceImpl(protectedZoneRepository, geoJsonService);
      sampleZone = new ProtectedZoneBean();
      sampleZone.setId(1L);
      sampleZone.setName("Marecages de Logbaba");

      zoneRequest = new ProtectedZoneRequest();
      zoneRequest.setName("Marecages de Logbaba 1");
      zoneRequest.setDataCoordinates(List.of(List.of(7.77, 4.77), List.of(7.87, 4.78), List.of(7.77, 4.87), List.of(7.77, 4.77)));
      when(geoJsonService.convertToGeoJsonPolygonFeature(any(ProtectedZoneBean.class)))
              .thenReturn(new GeoJsonFeature<>(new PolygonGeometry(new double[][][]{}), Map.of()));
   }

   @Test
   void createZone_ShouldPass_WithWrongPolygonValue() {
//      zoneRequest.setDataCoordinates(List.of(List.of(7.77, 4.77), List.of(7.87, 4.78), List.of(7.77, 4.87), List.of(8.77, 4.77)));
      List<List<Double>> coordinates = new ArrayList<>();
      coordinates.add(List.of(7.77, 4.77));
      coordinates.add(List.of(7.87, 4.78));
      coordinates.add(List.of(7.77, 4.87));
      coordinates.add(List.of(8.77, 4.77));
      zoneRequest.setDataCoordinates(coordinates);

      when(protectedZoneRepository.save(Mockito.any(ProtectedZoneBean.class))).thenReturn(sampleZone);

      zoneService.create(zoneRequest);
      verify(protectedZoneRepository).save(zoneBeanArgumentCaptor.capture());
      ProtectedZoneBean zoneBean = zoneBeanArgumentCaptor.getValue();

      Polygon polygon = zoneBean.getGeom();
      assertNotNull(polygon);
      assertEquals(7.77, polygon.getCoordinates()[0].x);
      assertEquals(4.77, polygon.getCoordinates()[0].y);
      assertEquals(8.77, polygon.getCoordinates()[3].x);
      assertEquals(4.77, polygon.getCoordinates()[3].y);
      assertEquals("Marecages de Logbaba 1", zoneBean.getName());
      verify(protectedZoneRepository, Mockito.times(1)).save(Mockito.any(ProtectedZoneBean.class));
   }

}
