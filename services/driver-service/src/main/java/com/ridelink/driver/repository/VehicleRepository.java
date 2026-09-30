package com.ridelink.driver.repository;

import com.ridelink.driver.entity.DriverProfile;
import com.ridelink.driver.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    List<Vehicle> findByDriver(DriverProfile driver);
}
