package com.ridelink.driver.repository;

import com.ridelink.driver.entity.DriverProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DriverProfileRepository extends JpaRepository<DriverProfile, String> {
    Optional<DriverProfile> findByUserId(String userId);
    List<DriverProfile> findByIsAvailableTrueAndServiceArea(String serviceArea);
}
