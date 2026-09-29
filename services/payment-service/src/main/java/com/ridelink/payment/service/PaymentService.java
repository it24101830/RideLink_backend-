package com.ridelink.payment.service;

import com.ridelink.payment.document.FareEstimate;
import com.ridelink.payment.document.Payment;
import com.ridelink.payment.document.Receipt;
import com.ridelink.payment.dto.*;
import com.ridelink.payment.enums.PaymentStatus;
import com.ridelink.payment.exception.ConflictException;
import com.ridelink.payment.exception.NotFoundException;
import com.ridelink.payment.repository.FareEstimateRepository;
import com.ridelink.payment.repository.PaymentRepository;
import com.ridelink.payment.repository.ReceiptRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PaymentService {

    private static final double BASE_FARE = 100.0;
    private static final double RATE_PER_KM = 80.0;
    private static final double RATE_PER_MIN = 10.0;
    private static final double MINIMUM_FARE = 250.0;

    private final FareEstimateRepository fareEstimateRepository;
    private final PaymentRepository paymentRepository;
    private final ReceiptRepository receiptRepository;

    private final java.util.concurrent.atomic.AtomicBoolean mongoAvailable = new java.util.concurrent.atomic.AtomicBoolean(true);

    private final Map<String, FareEstimate> inMemoryEstimates = new ConcurrentHashMap<>();
    private final Map<String, Payment> inMemoryPaymentsById = new ConcurrentHashMap<>();
    private final Map<String, Payment> inMemoryPaymentsByRideId = new ConcurrentHashMap<>();
    private final Map<String, Receipt> inMemoryReceiptsByRideId = new ConcurrentHashMap<>();

    public PaymentService(FareEstimateRepository fareEstimateRepository,
                           PaymentRepository paymentRepository,
                           ReceiptRepository receiptRepository) {
        this.fareEstimateRepository = fareEstimateRepository;
        this.paymentRepository = paymentRepository;
        this.receiptRepository = receiptRepository;
    }

    private double calculateFare(double distanceKm, double durationMin) {
        double raw = BASE_FARE + (distanceKm * RATE_PER_KM) + (durationMin * RATE_PER_MIN);
        return Math.max(raw, MINIMUM_FARE);
    }

    public FareEstimateResponse createEstimate(FareEstimateRequest req) {
        double fare = calculateFare(req.getDistanceKm(), req.getDurationMin());

        FareEstimate estimate = new FareEstimate();
        estimate.setId(UUID.randomUUID().toString());
        estimate.setPickupLocation(req.getPickupLocation());
        estimate.setDropoffLocation(req.getDropoffLocation());
        estimate.setEstimatedFare(fare);
        estimate.setCreatedAt(Instant.now());

        if (mongoAvailable.get()) {
            try {
                FareEstimate saved = fareEstimateRepository.save(estimate);
                if (saved != null && saved.getId() != null) {
                    estimate = saved;
                }
            } catch (Exception e) {
                mongoAvailable.set(false);
            }
        }
        inMemoryEstimates.put(estimate.getId(), estimate);

        return new FareEstimateResponse(estimate.getId(), estimate.getEstimatedFare());
    }

    public PaymentResultResponse recordPayment(CreatePaymentRequest req) {
        if (inMemoryPaymentsByRideId.containsKey(req.getRideId())) {
            throw new ConflictException("Payment already recorded");
        }
        if (mongoAvailable.get()) {
            try {
                if (paymentRepository.findFirstByRideId(req.getRideId()).isPresent()) {
                    throw new ConflictException("Payment already recorded");
                }
            } catch (ConflictException ce) {
                throw ce;
            } catch (Exception ignored) {
                mongoAvailable.set(false);
            }
        }

        double fare = calculateFare(req.getDistanceKm(), req.getDurationMin());

        Payment payment = new Payment();
        payment.setId(UUID.randomUUID().toString());
        payment.setRideId(req.getRideId());
        payment.setAmount(fare);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCreatedAt(Instant.now());

        if (mongoAvailable.get()) {
            try {
                Payment saved = paymentRepository.save(payment);
                if (saved != null && saved.getId() != null) {
                    payment = saved;
                }
            } catch (Exception ignored) {
                mongoAvailable.set(false);
            }
        }
        inMemoryPaymentsByRideId.put(req.getRideId(), payment);
        inMemoryPaymentsById.put(payment.getId(), payment);

        Map<String, Object> breakdown = new LinkedHashMap<>();
        breakdown.put("baseFare", BASE_FARE);
        breakdown.put("distanceCharge", req.getDistanceKm() * RATE_PER_KM);
        breakdown.put("durationCharge", req.getDurationMin() * RATE_PER_MIN);
        breakdown.put("total", fare);

        Receipt receipt = new Receipt();
        receipt.setId(UUID.randomUUID().toString());
        receipt.setPaymentId(payment.getId());
        receipt.setRideId(req.getRideId());
        receipt.setBreakdown(breakdown);
        receipt.setIssuedAt(Instant.now());

        if (mongoAvailable.get()) {
            try {
                Receipt saved = receiptRepository.save(receipt);
                if (saved != null && saved.getId() != null) {
                    receipt = saved;
                }
            } catch (Exception ignored) {
                mongoAvailable.set(false);
            }
        }
        inMemoryReceiptsByRideId.put(req.getRideId(), receipt);

        return new PaymentResultResponse(payment.getId(), payment.getStatus().name(),
            receipt.getId(), payment.getAmount());
    }

    public PaymentStatusResponse getPayment(String paymentId) {
        Payment payment = inMemoryPaymentsById.get(paymentId);
        if (payment == null && mongoAvailable.get()) {
            try {
                payment = paymentRepository.findById(paymentId)
                    .orElse(null);
            } catch (Exception ignored) {
                mongoAvailable.set(false);
            }
        }
        if (payment == null) {
            throw new NotFoundException("Payment not found");
        }
        return new PaymentStatusResponse(payment.getId(), payment.getRideId(),
            payment.getAmount(), payment.getStatus());
    }

    public ReceiptResponse getReceiptByRideId(String rideId) {
        Receipt receipt = inMemoryReceiptsByRideId.get(rideId);
        if (receipt == null && mongoAvailable.get()) {
            try {
                receipt = receiptRepository.findByRideId(rideId)
                    .orElse(null);
            } catch (Exception ignored) {
                mongoAvailable.set(false);
            }
        }
        if (receipt == null) {
            throw new NotFoundException("Receipt not found for this ride");
        }
        return new ReceiptResponse(receipt.getId(), receipt.getPaymentId(), receipt.getRideId(),
            receipt.getBreakdown(), receipt.getIssuedAt());
    }
}
