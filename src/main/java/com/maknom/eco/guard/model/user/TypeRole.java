package com.maknom.eco.guard.model.user;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum TypeRole {

   ADMIN, GARDE;


   @JsonCreator
   public static TypeRole parse(String role) {
      return  Arrays.stream(TypeRole.values())
              .filter(t -> t.name().equalsIgnoreCase(role))
              .findFirst()
              .orElse(GARDE);
   }

   @Override
   public String toString() {
      return name().toLowerCase();
   }
}


