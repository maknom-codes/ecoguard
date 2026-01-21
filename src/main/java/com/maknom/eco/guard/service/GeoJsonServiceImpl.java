package com.maknom.eco.guard.service;

import com.maknom.eco.guard.model.geom.GeoJsonFeature;
import com.maknom.eco.guard.model.geom.GeoJsonFeatureCollection;
import com.maknom.eco.guard.model.geom.GeoJsonGeometry;
import com.maknom.eco.guard.model.geom.GeoJsonService;
import com.maknom.eco.guard.model.incident.IncidentBean;
import org.locationtech.jts.geom.Point;
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

   private GeoJsonFeature convertToGeoJsonFeature(IncidentBean incidentBean) {
      Point point = incidentBean.getGeom();
      GeoJsonGeometry geometry = new GeoJsonGeometry(
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

      return new GeoJsonFeature(geometry, properties);
   }

}
