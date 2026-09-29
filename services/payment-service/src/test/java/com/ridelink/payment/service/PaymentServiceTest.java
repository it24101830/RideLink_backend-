package com.ridelink.payment.service;

import com.ridelink.payment.document.FareEstimate;
import com.ridelink.payment.document.Payment;
import com.ridelink.payment.document.Receipt;
import com.ridelink.payment.dto.CreatePaymentRequest;
import com.ridelink.payment.dto.FareEstimateRequest;
import com.ridelink.payment.dto.FareEstimateResponse;
import com.ridelink.payment.enums.PaymentStatus;
import com.ridelink.payment.exception.ConflictException;
import com.ridelink.payment.exception.NotFoundException;
import com.ridelink.payment.repository.FareEstimateRepository;
import com.ridelink.payment.repository.PaymentRepository;
import com.ridelink.payment.repository.ReceiptRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private FareEstimateRepository fareEstimateRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ReceiptRepository receiptRepository;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createEstimate_ShouldCalculateExactFare() {
        FareEstimateRequest request = new FareEstimateRequest();
        request.setDistanceKm(8.5);
        request.setDurationMin(22.0);
        request.setPickupLocation("A");
        request.setDropoffLocation("B");

        FareEstimate savedEstimate = new FareEstimate();
        savedEstimate.setId("est-123");
        savedEstimate.setEstimatedFare(1000.0);

        when(fareEstimateRepository.save(any(FareEstimate.class))).thenReturn(savedEstimate);

        FareEstimateResponse response = paymentService.createEstimate(request);

        assertEquals(1000.0, response.getEstimatedFare());
        verify(fareEstimateRepository, times(1)).save(any(FareEstimate.class));
    }

    @Test
    void createEstimate_ShouldApplyMinimumFareFloor() {
        FareEstimateRequest request = new FareEstimateRequest();
        request.setDistanceKm(0.5);
        request.setDurationMin(1.0);
        request.setPickupLocation("A");
        request.setDropoffLocation("B");

        FareEstimate savedEstimate = new FareEstimate();
        savedEstimate.setId("est-124");
        savedEstimate.setEstimatedFare(250.0);

        when(fareEstimateRepository.save(any(FareEstimate.class))).thenReturn(savedEstimate);

        FareEstimateResponse response = paymentService.createEstimate(request);

        assertEquals(250.0, response.getEstimatedFare());
        verify(fareEstimateRepository, times(1)).save(any(FareEstimate.class));
    }

    @Test
    void recordPayment_ThrowsConflictWhenPaymentExists() {
        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setRideId("ride-123");
        request.setDistanceKm(10.0);
        request.setDurationMin(15.0);

        when(paymentRepository.findFirstByRideId("ride-123")).thenReturn(Optional.of(new Payment()));

        assertThrows(ConflictException.class, () -> paymentService.recordPayment(request));
    }

    @Test
    void getReceiptByRideId_ThrowsNotFoundWhenNoReceipt() {
        when(receiptRepository.findByRideId("unknown-ride")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> paymentService.getReceiptByRideId("unknown-ride"));
    }
}
