package com.ridelink.driver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class AddVehicleRequest {
    @NotBlank
    private String make;
    @NotBlank
    private String model;
    @NotBlank
    private String plateNumber;
    @Positive
    private int capacity;
    @NotBlank
    private String category;

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
