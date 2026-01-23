package com.maknom.eco.guard.model.geom;

import lombok.Data;

import java.io.Serializable;


@Data
public class PolygonGeometry implements Serializable {

   private String type = "Polygon";

   private double[][][] coordinates;

   public PolygonGeometry(double[][][] coordinates) {
      this.coordinates = coordinates;
   }
}
