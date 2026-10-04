package com.ridelink.driver.service;

import com.ridelink.driver.dto.*;
import com.ridelink.driver.entity.DriverProfile;
import com.ridelink.driver.entity.Vehicle;
import com.ridelink.driver.exception.ForbiddenException;
import com.ridelink.driver.exception.NotFoundException;
import com.ridelink.driver.repository.DriverProfileRepository;
import com.ridelink.driver.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DriverService {

    private final DriverProfileRepository driverRepo;
    private final VehicleRepository vehicleRepo;

    public DriverService(DriverProfileRepository driverRepo, VehicleRepository vehicleRepo) {
        this.driverRepo = driverRepo;
        this.vehicleRepo = vehicleRepo;
    }

    public DriverProfileResponse createProfile(CreateDriverProfileRequest req, String userId) {
        DriverProfile profile = new DriverProfile();
        profile.setUserId(userId);
        profile.setLicenseNumber(req.getLicenseNumber());
        profile.setServiceArea(req.getServiceArea());
        DriverProfile saved = driverRepo.save(profile);
        return toResponse(saved);
    }

    public void addVehicle(String driverId, AddVehicleRequest req, String callerUserId) {
        DriverProfile driver = findAndCheckOwnership(driverId, callerUserId);
        Vehicle vehicle = new Vehicle();
        vehicle.setDriver(driver);
        vehicle.setMake(req.getMake());
        vehicle.setModel(req.getModel());
        vehicle.setPlateNumber(req.getPlateNumber());
        vehicle.setCapacity(req.getCapacity());
        vehicle.setCategory(req.getCategory());
        vehicleRepo.save(vehicle);
    }

    public DriverProfileResponse updateAvailability(String driverId, UpdateAvailabilityRequest req,
                                                      String callerUserId) {
        DriverProfile driver = findAndCheckOwnership(driverId, callerUserId);
        driver.setAvailable(req.isAvailable());
        return toResponse(driverRepo.save(driver));
    }

    public DriverProfileResponse updateLocation(String driverId, UpdateLocationRequest req, String callerUserId) {
        DriverProfile driver = findAndCheckOwnership(driverId, callerUserId);
        driver.setCurrentLat(req.getCurrentLat());
        driver.setCurrentLng(req.getCurrentLng());
        if (req.getServiceArea() != null) {
            driver.setServiceArea(req.getServiceArea());
        }
        return toResponse(driverRepo.save(driver));
    }

    public List<EligibleDriverDto> getEligibleDrivers(String area) {
        List<DriverProfile> drivers = driverRepo.findByIsAvailableTrueAndServiceArea(area);
        return drivers.stream()
            .map(d -> {
                List<Vehicle> vehicles = vehicleRepo.findByDriver(d);
                String vehicleLabel = vehicles.isEmpty()
                    ? "Unknown vehicle"
                    : vehicles.get(0).getMake() + " " + vehicles.get(0).getModel();
                return new EligibleDriverDto(d.getId(), vehicleLabel, d.getCurrentLat(), d.getCurrentLng());
            })
            .toList();
    }

    public DriverProfileResponse getProfile(String driverId) {
        return toResponse(findById(driverId));
    }

    // --- helpers ---
    private DriverProfile findById(String driverId) {
        return driverRepo.findById(driverId)
            .orElseThrow(() -> new NotFoundException("Driver profile not found"));
    }

    private DriverProfile findAndCheckOwnership(String driverId, String callerUserId) {
        DriverProfile driver = findById(driverId);
        if (!driver.getUserId().equals(callerUserId)) {
            throw new ForbiddenException("You do not own this driver profile");
        }
        return driver;
    }

    private DriverProfileResponse toResponse(DriverProfile d) {
        return new DriverProfileResponse(d.getId(), d.getUserId(), d.getLicenseNumber(),
            d.getStatus(), d.getServiceArea(), d.isAvailable());
    }
}
