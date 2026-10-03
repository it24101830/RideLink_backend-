package com.ridelink.account.dto;

public class AuthResponse {
    private String accessToken;
    private String role;
    private String userId;

    public AuthResponse(String accessToken, String role, String userId) {
        this.accessToken = accessToken;
        this.role = role;
        this.userId = userId;
    }
    public String getAccessToken() { return accessToken; }
    public String getRole() { return role; }
    public String getUserId() { return userId; }
}
