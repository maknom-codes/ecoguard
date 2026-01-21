package com.maknom.eco.guard.model.incident;


import java.util.List;

public interface IncidentService {

   IncidentBean create(String username, SyncDataRequest syncDataRequest);

   IncidentBean create(String username, IncidentRequest incidentRequest);


   IncidentBean getById(Long IncidentId);

   List<IncidentBean> getAllIncidents();
}
