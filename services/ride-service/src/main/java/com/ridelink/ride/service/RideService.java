package com.ridelink.ride.service;

import com.ridelink.ride.client.DriverClient;
import com.ridelink.ride.client.PaymentClient;
import com.ridelink.ride.dto.*;
import com.ridelink.ride.entity.Ride;
import com.ridelink.ride.enums.RideStatus;
import com.ridelink.ride.exception.ForbiddenException;
import com.ridelink.ride.exception.InvalidTransitionException;
import com.ridelink.ride.exception.NotFoundException;
import com.ridelink.ride.repository.RideRepository;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

@Service
public class RideService {

    // the state machine, enforced in code -- exactly the table in Section 3
    private static final Map<RideStatus, EnumSet<RideStatus>> TRANSITIONS = new EnumMap<>(RideStatus.class);
    static {
        TRANSITIONS.put(RideStatus.REQUESTED, EnumSet.of(RideStatus.ASSIGNED, RideStatus.CANCELLED));
        TRANSITIONS.put(RideStatus.ASSIGNED, EnumSet.of(RideStatus.ACCEPTED, RideStatus.CANCELLED));
        TRANSITIONS.put(RideStatus.ACCEPTED, EnumSet.of(RideStatus.IN_PROGRESS, RideStatus.CANCELLED));
        TRANSITIONS.put(RideStatus.IN_PROGRESS, EnumSet.of(RideStatus.COMPLETED));
        TRANSITIONS.put(RideStatus.COMPLETED, EnumSet.noneOf(RideStatus.class));
        TRANSITIONS.put(RideStatus.CANCELLED, EnumSet.noneOf(RideStatus.class));
    }

    private final RideRepository rideRepository;
    private final DriverClient driverClient;
    private final PaymentClient paymentClient;

    public RideService(RideRepository rideRepository, DriverClient driverClient, PaymentClient paymentClient) {
        this.rideRepository = rideRepository;
        this.driverClient = driverClient;
        this.paymentClient = paymentClient;
    }

    public RideResponse createRide(CreateRideRequest req, String passengerId) {
        Ride ride = new Ride();
        ride.setPassengerId(passengerId);
        ride.setPickupLocation(req.getPickupLocation());
        ride.setPickupArea(req.getPickupArea());
        ride.setDropoffLocation(req.getDropoffLocation());
        ride.setEstimatedFare(req.getEstimatedFare());
        return toResponse(rideRepository.save(ride));
    }

    // Required interservice call #1: ride -> driver
    public RideResponse assignDriver(String rideId, String authHeader) {
        Ride ride = findById(rideId);
        transition(ride, RideStatus.ASSIGNED);

        List<EligibleDriverDto> drivers = driverClient.getEligibleDrivers(ride.getPickupArea(), authHeader);

        if (drivers.isEmpty()) {
            throw new NotFoundException("No eligible driver available"); // -> 404, required negative scenario
        }

        ride.setDriverId(drivers.get(0).getDriverId()); // matching rule: first eligible driver
        return toResponse(rideRepository.save(ride));
    }

    public RideResponse acceptRide(String rideId, String callerUserId) {
        Ride ride = findAndCheckDriverOwnership(rideId, callerUserId);
        transition(ride, RideStatus.ACCEPTED);
        return toResponse(rideRepository.save(ride));
    }

    public RideResponse startRide(String rideId, String callerUserId) {
        Ride ride = findAndCheckDriverOwnership(rideId, callerUserId);
        transition(ride, RideStatus.IN_PROGRESS);
        return toResponse(rideRepository.save(ride));
    }

    // Required interservice call #2: ride -> payment, triggered right after completion
    public RideResponse completeRide(String rideId, CompleteRideRequest req, String callerUserId, String authHeader) {
        Ride ride = findAndCheckDriverOwnership(rideId, callerUserId);
        transition(ride, RideStatus.COMPLETED);
        ride.setUpdatedAt(Instant.now());
        Ride saved = rideRepository.save(ride);

        CreatePaymentRequest paymentReq = new CreatePaymentRequest(saved.getId(), req.getDistanceKm(), req.getDurationMin());
        PaymentResultDto result = paymentClient.recordPayment(paymentReq, authHeader);

        saved.setFinalFare(result.getAmount());
        return toResponse(rideRepository.save(saved));
    }

    public RideResponse cancelRide(String rideId, String callerUserId) {
        Ride ride = findById(rideId);
        transition(ride, RideStatus.CANCELLED);
        return toResponse(rideRepository.save(ride));
    }

    public RideResponse getRide(String rideId, String callerUserId) {
        Ride ride = findById(rideId);
        if (callerUserId != null
                && !callerUserId.equals(ride.getPassengerId())
                && !callerUserId.equals(ride.getDriverId())
                && !driverClient.isDriverOwnedByUser(ride.getDriverId(), callerUserId)) {
            throw new ForbiddenException("You do not have access to this ride");
        }
        return toResponse(ride);
    }

    public List<RideResponse> listMyRides(String callerUserId) {
        return listMyRides(callerUserId, null, null);
    }

    public List<RideResponse> listMyRides(String callerUserId, RideStatus status, String rideId) {
        if (rideId != null && !rideId.isBlank()) {
            return rideRepository.findById(rideId)
                .map(r -> List.of(toResponse(r)))
                .orElse(List.of());
        }
        List<Ride> rides = rideRepository.findByPassengerId(callerUserId);
        if (rides.isEmpty()) {
            rides = rideRepository.findByDriverId(callerUserId);
        }
        if (status != null) {
            rides = rides.stream().filter(r -> r.getStatus() == status).toList();
        }
        return rides.stream().map(this::toResponse).toList();
    }

    // --- helpers ---
    private Ride findById(String rideId) {
        return rideRepository.findById(rideId)
            .orElseThrow(() -> new NotFoundException("Ride not found"));
    }

    private Ride findAndCheckDriverOwnership(String rideId, String callerUserId) {
        Ride ride = findById(rideId);
        if (ride.getDriverId() == null ||
                (!ride.getDriverId().equals(callerUserId)
                 && !driverClient.isDriverOwnedByUser(ride.getDriverId(), callerUserId))) {
            throw new ForbiddenException("You are not the assigned driver for this ride");
        }
        return ride;
    }

    private void transition(Ride ride, RideStatus to) {
        EnumSet<RideStatus> allowed = TRANSITIONS.get(ride.getStatus());
        if (allowed == null || !allowed.contains(to)) {
            throw new InvalidTransitionException(
                "Cannot transition ride from " + ride.getStatus() + " to " + to); // -> 409, required negative scenario
        }
        ride.setStatus(to);
        ride.setUpdatedAt(Instant.now());
    }

    private RideResponse toResponse(Ride r) {
        return new RideResponse(r.getId(), r.getPassengerId(), r.getDriverId(), r.getPickupLocation(),
            r.getDropoffLocation(), r.getStatus(), r.getEstimatedFare(), r.getFinalFare(),
            r.getRequestedAt(), r.getUpdatedAt());
    }
}
