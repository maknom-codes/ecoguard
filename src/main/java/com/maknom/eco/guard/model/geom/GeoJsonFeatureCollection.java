package com.maknom.eco.guard.model.geom;

import lombok.Data;

import java.util.List;

@Data
public class GeoJsonFeatureCollection {

   private String type = "FeatureCollection";
   private List<GeoJsonFeature> features;

   public GeoJsonFeatureCollection(List<GeoJsonFeature> features) {
      this.features = features;
   }
}
