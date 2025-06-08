package com.bankwise.aml_backend.controller;

public class AuthResponse {
    private final String token;
    // Constructor and getter

    public AuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}