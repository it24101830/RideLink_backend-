package com.ridelink.driver.controller;

import com.ridelink.driver.dto.*;
import com.ridelink.driver.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@Tag(name = "Driver & Vehicle")
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@SecurityRequirement(name = "bearerAuth")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @PostMapping
    @Operation(summary = "Create the caller's driver operational profile")
    public ResponseEntity<DriverProfileResponse> createProfile(
            @Valid @RequestBody CreateDriverProfileRequest req,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(driverService.createProfile(req, userId));
    }

    @PostMapping("/{id}/vehicles")
    @Operation(summary = "Add a vehicle to the caller's own driver profile")
    public ResponseEntity<Void> addVehicle(
            @PathVariable String id,
            @Valid @RequestBody AddVehicleRequest req,
            @AuthenticationPrincipal String userId) {
        driverService.addVehicle(id, req, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping("/{id}/availability")
    @Operation(summary = "Toggle the caller's own availability")
    public ResponseEntity<DriverProfileResponse> updateAvailability(
            @PathVariable String id,
            @Valid @RequestBody UpdateAvailabilityRequest req,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(driverService.updateAvailability(id, req, userId));
    }

    @PatchMapping("/{id}/location")
    @Operation(summary = "Update the caller's own simulated location/service area")
    public ResponseEntity<DriverProfileResponse> updateLocation(
            @PathVariable String id,
            @Valid @RequestBody UpdateLocationRequest req,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(driverService.updateLocation(id, req, userId));
    }

    @GetMapping("/eligible")
    @Operation(summary = "List available drivers in an area (called by ride-service)")
    public ResponseEntity<List<EligibleDriverDto>> getEligibleDrivers(@RequestParam String area) {
        return ResponseEntity.ok(driverService.getEligibleDrivers(area));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a driver's operational profile")
    public ResponseEntity<DriverProfileResponse> getProfile(@PathVariable String id) {
        return ResponseEntity.ok(driverService.getProfile(id));
    }
}
