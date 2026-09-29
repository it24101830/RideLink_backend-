package com.ridelink.ride.entity;

import com.ridelink.ride.enums.RideStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "rides")
public class Ride {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String passengerId; // account-service's User.id -- plain String, no FK

    private String driverId;    // driver-service's DriverProfile.id -- plain String, no FK, nullable until assigned

    @Column(nullable = false)
    private String pickupLocation;

    @Column(nullable = false)
    private String pickupArea; // matches driver-service's DriverProfile.serviceArea values, e.g. "Colombo07"

    @Column(nullable = false)
    private String dropoffLocation;

    @Enumerated(EnumType.STRING)
    private RideStatus status = RideStatus.REQUESTED;

    private Double estimatedFare;
    private Double finalFare;

    private Instant requestedAt = Instant.now();
    private Instant updatedAt = Instant.now();

    // --- getters and setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPassengerId() { return passengerId; }
    public void setPassengerId(String passengerId) { this.passengerId = passengerId; }
    public String getDriverId() { return driverId; }
    public void setDriverId(String driverId) { this.driverId = driverId; }
    public String getPickupLocation() { return pickupLocation; }
    public void setPickupLocation(String pickupLocation) { this.pickupLocation = pickupLocation; }
    public String getPickupArea() { return pickupArea; }
    public void setPickupArea(String pickupArea) { this.pickupArea = pickupArea; }
    public String getDropoffLocation() { return dropoffLocation; }
    public void setDropoffLocation(String dropoffLocation) { this.dropoffLocation = dropoffLocation; }
    public RideStatus getStatus() { return status; }
    public void setStatus(RideStatus status) { this.status = status; }
    public Double getEstimatedFare() { return estimatedFare; }
    public void setEstimatedFare(Double estimatedFare) { this.estimatedFare = estimatedFare; }
    public Double getFinalFare() { return finalFare; }
    public void setFinalFare(Double finalFare) { this.finalFare = finalFare; }
    public Instant getRequestedAt() { return requestedAt; }
    public void setRequestedAt(Instant requestedAt) { this.requestedAt = requestedAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
