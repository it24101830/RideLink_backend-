package com.ridelink.ride.service;

import com.ridelink.ride.client.DriverClient;
import com.ridelink.ride.client.PaymentClient;
import com.ridelink.ride.dto.EligibleDriverDto;
import com.ridelink.ride.entity.Ride;
import com.ridelink.ride.enums.RideStatus;
import com.ridelink.ride.exception.ForbiddenException;
import com.ridelink.ride.exception.InvalidTransitionException;
import com.ridelink.ride.exception.NotFoundException;
import com.ridelink.ride.exception.ServiceUnavailableException;
import com.ridelink.ride.repository.RideRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import com.ridelink.ride.dto.CreatePaymentRequest;

@ExtendWith(MockitoExtension.class)
class RideServiceTest {

    @Mock
    private RideRepository rideRepository;
    @Mock
    private DriverClient driverClient;
    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private RideService rideService;

    private Ride ride;

    @BeforeEach
    void setUp() {
        ride = new Ride();
        ride.setId("ride-1");
        ride.setPassengerId("passenger-1");
        ride.setPickupLocation("A");
        ride.setPickupArea("Area1");
        ride.setDropoffLocation("B");

        ride.setDriverId("driver-1");
    }

    @Test
    void acceptRide_validTransition() {
        ride.setStatus(RideStatus.ASSIGNED);
        when(rideRepository.findById("ride-1")).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);
        rideService.acceptRide("ride-1", "driver-1");
    }

    @Test
    void completeRide_invalidTransition_throwsException() {
        ride.setStatus(RideStatus.COMPLETED);
        when(rideRepository.findById("ride-1")).thenReturn(Optional.of(ride));
        assertThrows(InvalidTransitionException.class, () -> rideService.acceptRide("ride-1", "driver-1"));
    }

    @Test
    void assignDriver_noEligibleDriver_throwsNotFound() {
        ride.setStatus(RideStatus.REQUESTED);
        when(rideRepository.findById("ride-1")).thenReturn(Optional.of(ride));
        when(driverClient.getEligibleDrivers(anyString(), anyString())).thenReturn(Collections.emptyList());
        assertThrows(NotFoundException.class, () -> rideService.assignDriver("ride-1", "Bearer token"));
    }

    @Test
    void completeRide_downstreamFailure_throwsServiceUnavailable() {
        ride.setStatus(RideStatus.IN_PROGRESS);
        when(rideRepository.findById("ride-1")).thenReturn(Optional.of(ride));
        when(rideRepository.save(any(Ride.class))).thenReturn(ride);
        when(paymentClient.recordPayment(any(), any())).thenThrow(new ServiceUnavailableException("Payment failed"));
        
        com.ridelink.ride.dto.CompleteRideRequest req = new com.ridelink.ride.dto.CompleteRideRequest();
        req.setDistanceKm(10.0);
        req.setDurationMin(15.0);
        
        assertThrows(ServiceUnavailableException.class, () -> rideService.completeRide("ride-1", req, "driver-1", "Bearer token"));
    }

    @Test
    void acceptRide_wrongDriver_throwsForbidden() {
        ride.setStatus(RideStatus.ASSIGNED);
        when(rideRepository.findById("ride-1")).thenReturn(Optional.of(ride));
        assertThrows(ForbiddenException.class, () -> rideService.acceptRide("ride-1", "driver-2"));
    }
}
