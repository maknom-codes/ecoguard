package com.maknom.eco.guard.model.incident;


public interface IncidentZone {
   Long getId();
   String getDescription();
   String getCategory();
   String getReportDate();
   String getUrgency();
   String getGeometry();
   Long getZoneId();
   Long getUserId();
}
