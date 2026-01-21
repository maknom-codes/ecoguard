package com.maknom.eco.guard.controller;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum SyncEventType {


   ZONE, INCIDENT;


   @JsonCreator
   public static SyncEventType parse(String eventType) {
      return  Arrays.stream(SyncEventType.values())
              .filter(t -> t.name().equalsIgnoreCase(eventType))
              .findFirst()
              .orElse(INCIDENT);
   }

   @Override
   public String toString() {
      return name().toLowerCase();
   }
}
