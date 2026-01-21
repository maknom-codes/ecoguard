package com.maknom.eco.guard.model.geom;

import lombok.Data;

@Data
public class GeoJsonGeometry {

   private String type = "Point";
   private double[] coordinates;

   public GeoJsonGeometry(double longitude, double latitude) {
      this.coordinates = new double[] { longitude, latitude };
   }
}
