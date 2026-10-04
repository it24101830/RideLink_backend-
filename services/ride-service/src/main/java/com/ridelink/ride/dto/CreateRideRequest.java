package com.ridelink.ride.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateRideRequest {
    @NotBlank
    private String pickupLocation;
    @NotBlank
    private String pickupArea;
    @NotBlank
    private String dropoffLocation;
    private Double estimatedFare; // optional -- client may have already called payment-service's /fare-estimates

    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public String getPickupArea() { return pickupArea; }
    public void setPickupArea(String pickupArea) { this.pickupArea = pickupArea; }
    public String getDropoffLocation() { return dropoffLocation; }
    public void setDropoffLocation(String dropoffLocation) { this.dropoffLocation = dropoffLocation; }
    public Double getEstimatedFare() { return estimatedFare; }
    public void setEstimatedFare(Double estimatedFare) { this.estimatedFare = estimatedFare; }
}
