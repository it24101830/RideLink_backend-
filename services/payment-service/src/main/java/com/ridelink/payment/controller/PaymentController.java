package com.ridelink.payment.controller;

import com.ridelink.payment.dto.*;
import com.ridelink.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Fare & Payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/api/fare-estimates")
    @Operation(summary = "Estimate a fare for a pickup/destination pair")
    public ResponseEntity<FareEstimateResponse> estimate(@Valid @RequestBody FareEstimateRequest req) {
        return ResponseEntity.ok(paymentService.createEstimate(req));
    }

    @PostMapping("/api/payments")
    @Operation(summary = "Record a simulated payment and compute the final fare")
    public ResponseEntity<PaymentResultResponse> pay(@Valid @RequestBody CreatePaymentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.recordPayment(req));
    }

    @GetMapping("/api/payments/{id}")
    @Operation(summary = "Get payment status")
    public ResponseEntity<PaymentStatusResponse> getPayment(@PathVariable String id) {
        return ResponseEntity.ok(paymentService.getPayment(id));
    }

    @GetMapping("/api/receipts/{rideId}")
    @Operation(summary = "Get the receipt for a ride")
    public ResponseEntity<ReceiptResponse> getReceipt(@PathVariable String rideId) {
        return ResponseEntity.ok(paymentService.getReceiptByRideId(rideId));
    }
}
