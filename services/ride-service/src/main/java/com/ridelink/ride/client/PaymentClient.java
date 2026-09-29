package com.ridelink.ride.client;

import com.ridelink.ride.dto.CreatePaymentRequest;
import com.ridelink.ride.dto.PaymentResultDto;
import com.ridelink.ride.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class PaymentClient {

    private final WebClient paymentServiceClient;

    public PaymentClient(@Qualifier("paymentServiceClient") WebClient paymentServiceClient) {
        this.paymentServiceClient = paymentServiceClient;
    }

    public PaymentResultDto recordPayment(CreatePaymentRequest req, String bearerToken) {
        try {
            return paymentServiceClient.post()
                .uri("/api/payments")
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(PaymentResultDto.class)
                .block();
        } catch (WebClientRequestException | WebClientResponseException e) {
            throw new ServiceUnavailableException("Payment service is unreachable");
        }
    }
}
