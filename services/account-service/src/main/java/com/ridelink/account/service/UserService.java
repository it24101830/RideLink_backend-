package com.ridelink.account.service;

import com.ridelink.account.dto.UpdateProfileRequest;
import com.ridelink.account.dto.UpdateStatusRequest;
import com.ridelink.account.dto.UserResponse;
import com.ridelink.account.entity.User;
import com.ridelink.account.exception.NotFoundException;
import com.ridelink.account.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse getProfile(String userId) {
        return toResponse(findById(userId));
    }

    public UserResponse updateProfile(String userId, UpdateProfileRequest req) {
        User user = findById(userId);
        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getPhone() != null) user.setPhone(req.getPhone());
        return toResponse(userRepository.save(user));
    }

    public UserResponse updateStatus(String targetUserId, UpdateStatusRequest req) {
        User user = findById(targetUserId);
        user.setStatus(req.getStatus());
        return toResponse(userRepository.save(user));
    }

    private User findById(String userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private UserResponse toResponse(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getFullName(), u.getPhone(),
            u.getRole(), u.getStatus());
    }
}
