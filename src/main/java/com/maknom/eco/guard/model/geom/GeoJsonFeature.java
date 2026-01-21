package com.maknom.eco.guard.model.geom;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;


@Data
@RequiredArgsConstructor
public class GeoJsonFeature {

   private String type = "Feature";
   private GeoJsonGeometry geometry;
   private Map<String, Object> properties;

   public GeoJsonFeature(GeoJsonGeometry geometry, Map<String, Object> properties) {
      this.geometry = geometry;
      this.properties = properties;
   }
}
