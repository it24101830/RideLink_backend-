package com.ridelink.payment.dto;

public class FareEstimateResponse {
    private String id;
    private Double estimatedFare;

    public FareEstimateResponse(String id, Double estimatedFare) {
        this.id = id;
        this.estimatedFare = estimatedFare;
    }
    public String getId() { return id; }
    public Double getEstimatedFare() { return estimatedFare; }
}
