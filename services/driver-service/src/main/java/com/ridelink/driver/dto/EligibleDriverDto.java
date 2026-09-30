package com.ridelink.driver.dto;

public class EligibleDriverDto {
    private String driverId;
    private String vehicle; // e.g. "Toyota Aqua"
    private Double currentLat;
    private Double currentLng;

    public EligibleDriverDto(String driverId, String vehicle, Double currentLat, Double currentLng) {
        this.driverId = driverId;
        this.vehicle = vehicle;
        this.currentLat = currentLat;
        this.currentLng = currentLng;
    }

    public String getDriverId() { return driverId; }
    public String getVehicle() { return vehicle; }
    public Double getCurrentLat() { return currentLat; }
    public Double getCurrentLng() { return currentLng; }
}
