package com.maknom.eco.guard.model.zone;


public class ProtectedZone {
   private Long id;
   private String name;
   private String geoJson;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getGeoJson() {
      return geoJson;
   }

   public void setGeoJson(String geoJson) {
      this.geoJson = geoJson;
   }
}
