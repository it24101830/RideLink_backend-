package com.ridelink.payment.repository;

import com.ridelink.payment.document.FareEstimate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FareEstimateRepository extends MongoRepository<FareEstimate, String> {
}
