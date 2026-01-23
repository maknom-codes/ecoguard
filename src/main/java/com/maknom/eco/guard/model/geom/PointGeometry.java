package com.maknom.eco.guard.model.geom;

import lombok.Data;

@Data
public class PointGeometry {

   private String type = "Point";
   private double[] coordinates;

   public PointGeometry(double longitude, double latitude) {
      this.coordinates = new double[] { longitude, latitude };
   }
}
