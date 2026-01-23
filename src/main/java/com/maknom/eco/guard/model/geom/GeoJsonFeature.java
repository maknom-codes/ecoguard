package com.maknom.eco.guard.model.geom;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;
import java.util.Map;


@Data
@RequiredArgsConstructor
public class GeoJsonFeature<T> implements Serializable {

   private String type = "Feature";
   private T geometry;
   private Map<String, Object> properties;

   public GeoJsonFeature(T geometry, Map<String, Object> properties) {
      this.geometry = geometry;
      this.properties = properties;
   }
}
