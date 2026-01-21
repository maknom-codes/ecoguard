package com.maknom.eco.guard.model.zone;


import jakarta.validation.constraints.NotNull;




public class LocationRequest {
   @NotNull
   private Double longitude;
   @NotNull
   private Double latitude;


   public Double getLongitude() {
      return longitude;
   }

   public void setLongitude(Double longitude) {
      this.longitude = longitude;
   }

   public Double getLatitude() {
      return latitude;
   }

   public void setLatitude(Double latitude) {
      this.latitude = latitude;
   }
}
