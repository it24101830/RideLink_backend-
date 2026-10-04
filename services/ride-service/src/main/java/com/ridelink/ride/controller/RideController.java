package com.ridelink.ride.controller;

import com.ridelink.ride.dto.*;
import com.ridelink.ride.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.ridelink.ride.enums.RideStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rides")
@Tag(name = "Ride Management")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PASSENGER')")
    @Operation(summary = "Create a ride request")
    public ResponseEntity<RideResponse> create(@Valid @RequestBody CreateRideRequest req,
                                                @AuthenticationPrincipal String userId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.createRide(req, userId));
    }

    @PostMapping("/{id}/assign")
    @Operation(summary = "Assign an eligible driver to a ride")
    public ResponseEntity<RideResponse> assign(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            jakarta.servlet.http.HttpServletRequest request) {
        String token = (authHeader != null && !authHeader.isBlank()) ? authHeader : request.getHeader("Authorization");
        return ResponseEntity.ok(rideService.assignDriver(id, token));
    }

    @PatchMapping("/{id}/accept")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Driver accepts the assigned ride")
    public ResponseEntity<RideResponse> accept(@PathVariable String id,
                                                @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(rideService.acceptRide(id, userId));
    }

    @PatchMapping("/{id}/start")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Driver starts the ride")
    public ResponseEntity<RideResponse> start(@PathVariable String id,
                                               @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(rideService.startRide(id, userId));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('DRIVER')")
    @Operation(summary = "Driver completes the ride and triggers payment")
    public ResponseEntity<RideResponse> complete(
            @PathVariable String id,
            @Valid @RequestBody CompleteRideRequest req,
            @AuthenticationPrincipal String userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            jakarta.servlet.http.HttpServletRequest request) {
        String token = (authHeader != null && !authHeader.isBlank()) ? authHeader : request.getHeader("Authorization");
        return ResponseEntity.ok(rideService.completeRide(id, req, userId, token));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Passenger or driver cancels the ride")
    public ResponseEntity<RideResponse> cancel(@PathVariable String id,
                                                @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(rideService.cancelRide(id, userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ride details by ID", description = "Retrieve complete details for a specific ride by its UUID")
    public ResponseEntity<RideResponse> getById(
            @Parameter(description = "Ride UUID", required = true, example = "4d5e6f70-5b7e-4e21-9c88-1a2b3c4d5e6f")
            @PathVariable String id,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(rideService.getRide(id, userId));
    }

    @GetMapping
    @Operation(summary = "List rides for the logged-in user", description = "List rides for the authenticated user, with optional filters for ride status or specific ride ID")
    public ResponseEntity<List<RideResponse>> listMine(
            @Parameter(description = "Filter rides by status (optional)", example = "REQUESTED")
            @RequestParam(required = false) RideStatus status,
            @Parameter(description = "Filter by specific ride ID (optional)")
            @RequestParam(required = false) String rideId,
            @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(rideService.listMyRides(userId, status, rideId));
    }
}
