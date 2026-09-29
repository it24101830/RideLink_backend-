package com.ridelink.payment.dto;

import com.ridelink.payment.enums.PaymentStatus;

public class PaymentStatusResponse {
    private String id;
    private String rideId;
    private Double amount;
    private PaymentStatus status;

    public PaymentStatusResponse(String id, String rideId, Double amount, PaymentStatus status) {
        this.id = id;
        this.rideId = rideId;
        this.amount = amount;
        this.status = status;
    }
    public String getId() { return id; }
    public String getRideId() { return rideId; }
    public Double getAmount() { return amount; }
    public PaymentStatus getStatus() { return status; }
}
