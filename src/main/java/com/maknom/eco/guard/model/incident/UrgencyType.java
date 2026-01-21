package com.maknom.eco.guard.model.incident;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;

public enum UrgencyType {

   CRITICAL, HIGH, MEDIUM, LOW;


   @JsonCreator
   public static UrgencyType parseUrgencyType(String urgency) {
      return  Arrays.stream(UrgencyType.values())
              .filter(t -> t.name().equalsIgnoreCase(urgency))
              .findFirst()
              .orElse(LOW);
   }

   @Override
   public String toString() {
      return name().toLowerCase();
   }
}
