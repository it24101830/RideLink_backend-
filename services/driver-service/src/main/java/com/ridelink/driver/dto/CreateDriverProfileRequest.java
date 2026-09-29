package com.ridelink.driver.dto;

import jakarta.validation.constraints.NotBlank;

public class CreateDriverProfileRequest {
    @NotBlank
    private String licenseNumber;
    @NotBlank
    private String serviceArea;

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public String getServiceArea() { return serviceArea; }
    public void setServiceArea(String serviceArea) { this.serviceArea = serviceArea; }
}
