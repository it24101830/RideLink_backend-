package com.ridelink.account.service;

import com.ridelink.account.dto.AuthResponse;
import com.ridelink.account.dto.LoginRequest;
import com.ridelink.account.dto.RegisterRequest;
import com.ridelink.account.dto.UserResponse;
import com.ridelink.account.entity.User;
import com.ridelink.account.enums.AccountStatus;
import com.ridelink.account.enums.Role;
import com.ridelink.account.exception.ConflictException;
import com.ridelink.account.exception.UnauthorizedException;
import com.ridelink.account.repository.UserRepository;
import com.ridelink.account.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Test User");
        registerRequest.setPhone("0771234567");
        registerRequest.setRole(Role.PASSENGER);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        savedUser = new User();
        savedUser.setId("user-uuid-123");
        savedUser.setEmail("test@example.com");
        savedUser.setPasswordHash("$2a$10$hashedPassword");
        savedUser.setFullName("Test User");
        savedUser.setPhone("0771234567");
        savedUser.setRole(Role.PASSENGER);
        savedUser.setStatus(AccountStatus.ACTIVE);
    }

    // -----------------------------------------------------------------------
    // Register happy path
    // -----------------------------------------------------------------------
    @Test
    void register_happyPath_savesUserWithEncodedPassword() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = authService.register(registerRequest);

        // Encoder must have been called with the raw password
        verify(passwordEncoder).encode("password123");

        // Capture what was saved to assert the raw password was NOT stored
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User persisted = captor.getValue();
        assertThat(persisted.getPasswordHash()).isNotEqualTo("password123");
        assertThat(persisted.getPasswordHash()).isEqualTo("$2a$10$hashedPassword");

        // Response shape
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getRole()).isEqualTo(Role.PASSENGER);
    }

    // -----------------------------------------------------------------------
    // Register conflict
    // -----------------------------------------------------------------------
    @Test
    void register_conflict_throwsConflictException() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
            .isInstanceOf(ConflictException.class)
            .hasMessage("Email already registered");

        verify(userRepository, never()).save(any());
    }

    // -----------------------------------------------------------------------
    // Login happy path
    // -----------------------------------------------------------------------
    @Test
    void login_happyPath_returnsAuthResponseWithToken() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("password123", "$2a$10$hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("user-uuid-123", "PASSENGER", "test@example.com"))
            .thenReturn("mocked.jwt.token");

        AuthResponse response = authService.login(loginRequest);

        assertThat(response.getAccessToken()).isEqualTo("mocked.jwt.token");
        assertThat(response.getRole()).isEqualTo("PASSENGER");
        assertThat(response.getUserId()).isEqualTo("user-uuid-123");
    }

    // -----------------------------------------------------------------------
    // Login wrong password -- MUST produce IDENTICAL message to "user not found"
    // This is a deliberate security property: never reveal which part was wrong.
    // -----------------------------------------------------------------------
    @Test
    void login_wrongPassword_throwsUnauthorizedWithSameMessageAsUserNotFound() {
        // Wrong password path
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(savedUser));
        when(passwordEncoder.matches("password123", "$2a$10$hashedPassword")).thenReturn(false);

        String wrongPasswordMessage = null;
        try {
            authService.login(loginRequest);
        } catch (UnauthorizedException e) {
            wrongPasswordMessage = e.getMessage();
        }

        // User not found path
        LoginRequest notFoundReq = new LoginRequest();
        notFoundReq.setEmail("nonexistent@example.com");
        notFoundReq.setPassword("password123");
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        String userNotFoundMessage = null;
        try {
            authService.login(notFoundReq);
        } catch (UnauthorizedException e) {
            userNotFoundMessage = e.getMessage();
        }

        // Both failure paths must produce the exact same message
        assertThat(wrongPasswordMessage).isNotNull();
        assertThat(userNotFoundMessage).isNotNull();
        assertThat(wrongPasswordMessage)
            .as("Wrong-password and user-not-found must return the SAME error message (security property)")
            .isEqualTo(userNotFoundMessage);
    }
}
