package com.ridelink.payment.dto;

public class PaymentResultResponse {
    private String paymentId;
    private String status;
    private String receiptId;
    private Double amount;

    public PaymentResultResponse(String paymentId, String status, String receiptId, Double amount) {
        this.paymentId = paymentId;
        this.status = status;
        this.receiptId = receiptId;
        this.amount = amount;
    }
    public String getPaymentId() { return paymentId; }
    public String getStatus() { return status; }
    public String getReceiptId() { return receiptId; }
    public Double getAmount() { return amount; }
}
