package com.ridelink.ride.client;

import com.ridelink.ride.dto.EligibleDriverDto;
import com.ridelink.ride.exception.ServiceUnavailableException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import java.util.List;

@Service
public class DriverClient {

    private final WebClient driverServiceClient;

    public DriverClient(@Qualifier("driverServiceClient") WebClient driverServiceClient) {
        this.driverServiceClient = driverServiceClient;
    }

    public List<EligibleDriverDto> getEligibleDrivers(String area, String bearerToken) {
        try {
            return driverServiceClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/drivers/eligible")
                    .queryParam("area", area).build())
                .header(HttpHeaders.AUTHORIZATION, bearerToken)
                .retrieve()
                .bodyToFlux(EligibleDriverDto.class)
                .collectList()
                .block();
        } catch (WebClientRequestException | WebClientResponseException e) {
            throw new ServiceUnavailableException("Driver service is unreachable");
        }
    }
}
