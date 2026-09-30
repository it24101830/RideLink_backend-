package com.ridelink.driver.service;

import com.ridelink.driver.dto.EligibleDriverDto;
import com.ridelink.driver.dto.UpdateAvailabilityRequest;
import com.ridelink.driver.entity.DriverProfile;
import com.ridelink.driver.entity.Vehicle;
import com.ridelink.driver.exception.ForbiddenException;
import com.ridelink.driver.exception.NotFoundException;
import com.ridelink.driver.repository.DriverProfileRepository;
import com.ridelink.driver.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DriverServiceTest {

    @Mock
    private DriverProfileRepository driverRepo;

    @Mock
    private VehicleRepository vehicleRepo;

    @InjectMocks
    private DriverService driverService;

    @Test
    void testUpdateAvailability_HappyPath() {
        String driverId = "driver-123";
        String userId = "user-456";
        
        DriverProfile profile = new DriverProfile();
        profile.setId(driverId);
        profile.setUserId(userId);
        profile.setAvailable(false);
        
        UpdateAvailabilityRequest req = new UpdateAvailabilityRequest();
        req.setAvailable(true);
        
        when(driverRepo.findById(driverId)).thenReturn(Optional.of(profile));
        when(driverRepo.save(any(DriverProfile.class))).thenAnswer(i -> i.getArguments()[0]);
        
        var response = driverService.updateAvailability(driverId, req, userId);
        
        assertTrue(response.isAvailable());
        verify(driverRepo).save(profile);
    }

    @Test
    void testUpdateAvailability_OwnershipFailure() {
        String driverId = "driver-123";
        String ownerUserId = "user-456";
        String callerUserId = "user-789";
        
        DriverProfile profile = new DriverProfile();
        profile.setId(driverId);
        profile.setUserId(ownerUserId);
        
        UpdateAvailabilityRequest req = new UpdateAvailabilityRequest();
        req.setAvailable(true);
        
        when(driverRepo.findById(driverId)).thenReturn(Optional.of(profile));
        
        assertThrows(ForbiddenException.class, () -> {
            driverService.updateAvailability(driverId, req, callerUserId);
        });
        
        verify(driverRepo, never()).save(any());
    }

    @Test
    void testUpdateAvailability_NotFoundFailure() {
        String driverId = "nonexistent-id";
        String callerUserId = "user-456";
        
        UpdateAvailabilityRequest req = new UpdateAvailabilityRequest();
        req.setAvailable(true);
        
        when(driverRepo.findById(driverId)).thenReturn(Optional.empty());
        
        assertThrows(NotFoundException.class, () -> {
            driverService.updateAvailability(driverId, req, callerUserId);
        });
        
        verify(driverRepo, never()).save(any());
    }

    @Test
    void testGetEligibleDrivers_EmptyListCase() {
        String area = "Downtown";
        
        when(driverRepo.findByIsAvailableTrueAndServiceArea(area)).thenReturn(Collections.emptyList());
        
        List<EligibleDriverDto> result = driverService.getEligibleDrivers(area);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
