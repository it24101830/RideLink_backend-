package com.ridelink.driver.dto;

import com.ridelink.driver.enums.DriverStatus;

public class DriverProfileResponse {
    private String id;
    private String userId;
    private String licenseNumber;
    private DriverStatus status;
    private String serviceArea;
    private boolean available;

    public DriverProfileResponse(String id, String userId, String licenseNumber,
                                  DriverStatus status, String serviceArea, boolean available) {
        this.id = id;
        this.userId = userId;
        this.licenseNumber = licenseNumber;
        this.status = status;
        this.serviceArea = serviceArea;
        this.available = available;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getLicenseNumber() { return licenseNumber; }
    public DriverStatus getStatus() { return status; }
    public String getServiceArea() { return serviceArea; }
    public boolean isAvailable() { return available; }
}
