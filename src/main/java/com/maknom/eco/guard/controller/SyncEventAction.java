package com.maknom.eco.guard.controller;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum SyncEventAction {

   CREATED, UPDATED, DELETED;

   @JsonCreator
   public static SyncEventAction parse(String eventAction) {
      return  Arrays.stream(SyncEventAction.values())
              .filter(t -> t.name().equalsIgnoreCase(eventAction))
              .findFirst()
              .orElse(CREATED);
   }

   @Override
   public String toString() {
      return name().toLowerCase();
   }
}
