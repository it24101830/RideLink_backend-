package com.ridelink.ride.repository;

import com.ridelink.ride.entity.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RideRepository extends JpaRepository<Ride, String> {
    List<Ride> findByPassengerId(String passengerId);
    List<Ride> findByDriverId(String driverId);
}
