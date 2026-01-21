package com.maknom.eco.guard.model.user;


import jakarta.validation.constraints.NotEmpty;

public class UserRequest {

   @NotEmpty
   private String name;

   @NotEmpty
   private String email;

   @NotEmpty
   private String password;

   @NotEmpty
   private String role;


   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getRole() {
      return role;
   }

   public void setRole(String role) {
      this.role = role;
   }
}
