package com.maknom.eco.guard.model.zone;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class ProtectedZoneRequest {

   @NotBlank(message = "The name is required")
   @Size(min = 3, max = 100)
   private String name;

   @NotEmpty(message = "Data Coordinates cannot be empty")
   @Size(min = 3, message = "Min 3 points are requested for a Polygon")
   List<List<Double>> dataCoordinates;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public List<List<Double>> getDataCoordinates() {
      return dataCoordinates;
   }

   public void setDataCoordinates(List<List<Double>> dataCoordinates) {
      this.dataCoordinates = dataCoordinates;
   }
}
