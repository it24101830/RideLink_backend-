package com.ridelink.ride.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CompleteRideRequest {
    @NotNull @Positive
    private Double distanceKm;
    @NotNull @Positive
    private Double durationMin;

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }
    public Double getDurationMin() { return durationMin; }
    public void setDurationMin(Double durationMin) { this.durationMin = durationMin; }
}
