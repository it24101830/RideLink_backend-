package com.ridelink.ride.dto;

import com.ridelink.ride.enums.RideStatus;
import java.time.Instant;

public class RideResponse {
    private String id;
    private String passengerId;
    private String driverId;
    private String pickupLocation;
    private String dropoffLocation;
    private RideStatus status;
    private Double estimatedFare;
    private Double finalFare;
    private Instant requestedAt;
    private Instant updatedAt;

    public RideResponse(String id, String passengerId, String driverId, String pickupLocation,
                         String dropoffLocation, RideStatus status, Double estimatedFare,
                         Double finalFare, Instant requestedAt, Instant updatedAt) {
        this.id = id;
        this.passengerId = passengerId;
        this.driverId = driverId;
        this.pickupLocation = pickupLocation;
        this.dropoffLocation = dropoffLocation;
        this.status = status;
        this.estimatedFare = estimatedFare;
        this.finalFare = finalFare;
        this.requestedAt = requestedAt;
        this.updatedAt = updatedAt;
    }
    public String getId() { return id; }
    public String getPassengerId() { return passengerId; }
    public String getDriverId() { return driverId; }
    public String getPickupLocation() { return pickupLocation; }
    public String getDropoffLocation() { return dropoffLocation; }
    public RideStatus getStatus() { return status; }
    public Double getEstimatedFare() { return estimatedFare; }
    public Double getFinalFare() { return finalFare; }
    public Instant getRequestedAt() { return requestedAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
