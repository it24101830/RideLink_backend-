package com.ridelink.payment.repository;

import com.ridelink.payment.document.Receipt;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ReceiptRepository extends MongoRepository<Receipt, String> {
    Optional<Receipt> findByRideId(String rideId);
}
