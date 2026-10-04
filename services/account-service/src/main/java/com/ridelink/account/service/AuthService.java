package com.ridelink.account.service;

import com.ridelink.account.dto.*;
import com.ridelink.account.entity.User;
import com.ridelink.account.exception.ConflictException;
import com.ridelink.account.exception.UnauthorizedException;
import com.ridelink.account.repository.UserRepository;
import com.ridelink.account.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public UserResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ConflictException("Email already registered"); // -> 409
        }
        User user = new User();
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setFullName(req.getFullName());
        user.setPhone(req.getPhone());
        user.setRole(req.getRole());
        User saved = userRepository.save(user);
        return new UserResponse(saved.getId(), saved.getEmail(), saved.getFullName(),
            saved.getPhone(), saved.getRole(), saved.getStatus());
    }

    public AuthResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
            .orElseThrow(() -> new UnauthorizedException("Invalid email or password")); // -> 401

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            // deliberately the SAME message as above -- never reveal which part was wrong
            throw new UnauthorizedException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getRole().name(), user.getEmail());
        return new AuthResponse(token, user.getRole().name(), user.getId());
    }
}
