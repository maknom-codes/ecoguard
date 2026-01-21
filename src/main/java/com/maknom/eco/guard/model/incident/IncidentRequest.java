package com.maknom.eco.guard.model.incident;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class IncidentRequest {

   @NotEmpty
   private String description;

   @NotNull
   private double latitude;
   @NotNull
   private double longitude;

   @NotEmpty
   private String category;

   @NotEmpty
   private String urgency;

}
