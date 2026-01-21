package com.maknom.eco.guard.model.incident;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SyncDataRequest {
   private Long id;
   private IncidentRequest incidentRequest;
   private String timestamp;
}
