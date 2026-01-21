package com.maknom.eco.guard.controller;


public record AuthResponse(String message, boolean success, UserResponse user) {
}
