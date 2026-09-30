package com.ridelink.ride.dto;

public class CreatePaymentRequest {
    private String rideId;
    private Double distanceKm;
    private Double durationMin;

    public CreatePaymentRequest(String rideId, Double distanceKm, Double durationMin) {
        this.rideId = rideId;
        this.distanceKm = distanceKm;
        this.durationMin = durationMin;
    }
    public String getRideId() { return rideId; }
    public Double getDistanceKm() { return distanceKm; }
    public Double getDurationMin() { return durationMin; }
}
