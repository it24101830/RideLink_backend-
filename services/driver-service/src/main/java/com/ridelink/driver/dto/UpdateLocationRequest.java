package com.ridelink.driver.dto;

import jakarta.validation.constraints.NotNull;

public class UpdateLocationRequest {
    @NotNull
    private Double currentLat;
    @NotNull
    private Double currentLng;
    private String serviceArea; // optional — driver may also update their area at the same time

    public Double getCurrentLat() { return currentLat; }
    public void setCurrentLat(Double currentLat) { this.currentLat = currentLat; }
    public Double getCurrentLng() { return currentLng; }
    public void setCurrentLng(Double currentLng) { this.currentLng = currentLng; }
    public String getServiceArea() { return serviceArea; }
    public void setServiceArea(String serviceArea) { this.serviceArea = serviceArea; }
}
