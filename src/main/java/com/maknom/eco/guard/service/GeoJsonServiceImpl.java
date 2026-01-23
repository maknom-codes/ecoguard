package com.maknom.eco.guard.service;

import com.maknom.eco.guard.model.geom.GeoJsonFeature;
import com.maknom.eco.guard.model.geom.GeoJsonFeatureCollection;
import com.maknom.eco.guard.model.geom.PointGeometry;
import com.maknom.eco.guard.model.geom.GeoJsonService;
import com.maknom.eco.guard.model.geom.PolygonGeometry;
import com.maknom.eco.guard.model.incident.IncidentBean;
import com.maknom.eco.guard.model.zone.ProtectedZoneBean;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class GeoJsonServiceImpl implements GeoJsonService {


   @Override
   public GeoJsonFeatureCollection convertIncidentsToGeoJson(List<IncidentBean> incidents) {
      List<GeoJsonFeature> features = incidents.stream()
              .map(this::convertToGeoJsonFeature)
              .collect(Collectors.toList());

      return new GeoJsonFeatureCollection(features);
   }

   @Override
   public GeoJsonFeatureCollection convertZonesToGeoJson(List<ProtectedZoneBean> zones) {
      List<GeoJsonFeature> features = zones.stream()
              .map(this::convertToGeoJsonPolygonFeature)
              .collect(Collectors.toList());
      return new GeoJsonFeatureCollection(features);
   }

   public GeoJsonFeature<PointGeometry> convertToGeoJsonFeature(IncidentBean incidentBean) {
      Point point = incidentBean.getGeom();
      PointGeometry geometry = new PointGeometry(
              point.getX(),
              point.getY()
      );
      Map<String, Object> properties = new HashMap<>();
      properties.put("id", incidentBean.getId());
      properties.put("zoneId", incidentBean.getZoneId());
      properties.put("userId", incidentBean.getUserId());
      properties.put("category", incidentBean.getCategory());
      properties.put("description", incidentBean.getDescription());
      properties.put("urgency", incidentBean.getUrgency());
      properties.put("reportDate", incidentBean.getReportDate().toString());

      return new GeoJsonFeature<>(geometry, properties);
   }


   public GeoJsonFeature<PolygonGeometry> convertToGeoJsonPolygonFeature(ProtectedZoneBean zoneBean) {
      Polygon polygon = zoneBean.getGeom();
      Coordinate[] jtsCoords = polygon.getExteriorRing().getCoordinates();
      double[][] exteriorRing = new double[jtsCoords.length][2];

      for (int i = 0; i < jtsCoords.length; i++) {
         exteriorRing[i][0] = jtsCoords[i].x;
         exteriorRing[i][1] = jtsCoords[i].y;
      }

      double[][][] coordinates = new double[][][] { exteriorRing };
      PolygonGeometry geometry = new PolygonGeometry(coordinates);

      Map<String, Object> properties = Map.of(
              "id", zoneBean.getId(),
              "name", zoneBean.getName()
      );

      return new GeoJsonFeature<>(geometry, properties);
   }

}
