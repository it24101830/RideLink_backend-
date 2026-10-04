package com.ridelink.payment.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;
import java.util.Map;

@Document(collection = "receipts")
public class Receipt {

    @Id
    private String id;

    private String paymentId;
    private String rideId;
    private Map<String, Object> breakdown; // nested document: baseFare, distanceCharge, durationCharge, total
    private Instant issuedAt = Instant.now();

    // --- getters and setters ---
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }
    public String getRideId() { return rideId; }
    public void setRideId(String rideId) { this.rideId = rideId; }
    public Map<String, Object> getBreakdown() { return breakdown; }
    public void setBreakdown(Map<String, Object> breakdown) { this.breakdown = breakdown; }
    public Instant getIssuedAt() { return issuedAt; }
    public void setIssuedAt(Instant issuedAt) { this.issuedAt = issuedAt; }
}
