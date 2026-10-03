package com.ridelink.payment.dto;

import java.time.Instant;
import java.util.Map;

public class ReceiptResponse {
    private String id;
    private String paymentId;
    private String rideId;
    private Map<String, Object> breakdown;
    private Instant issuedAt;

    public ReceiptResponse(String id, String paymentId, String rideId,
                            Map<String, Object> breakdown, Instant issuedAt) {
        this.id = id;
        this.paymentId = paymentId;
        this.rideId = rideId;
        this.breakdown = breakdown;
        this.issuedAt = issuedAt;
    }
    public String getId() { return id; }
    public String getPaymentId() { return paymentId; }
    public String getRideId() { return rideId; }
    public Map<String, Object> getBreakdown() { return breakdown; }
    public Instant getIssuedAt() { return issuedAt; }
}
