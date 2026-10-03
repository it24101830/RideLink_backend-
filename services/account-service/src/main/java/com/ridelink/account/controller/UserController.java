package com.ridelink.account.controller;

import com.ridelink.account.dto.*;
import com.ridelink.account.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get the caller's own profile")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @PatchMapping("/me")
    @Operation(summary = "Update the caller's own profile")
    public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody UpdateProfileRequest req,
                                                  @AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(userService.updateProfile(userId, req));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a user's public profile (used internally by other services)")
    public ResponseEntity<UserResponse> getById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getProfile(id));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activate or suspend an account (admin only)")
    public ResponseEntity<UserResponse> updateStatus(@PathVariable String id,
                                                       @Valid @RequestBody UpdateStatusRequest req) {
        return ResponseEntity.ok(userService.updateStatus(id, req));
    }
}
