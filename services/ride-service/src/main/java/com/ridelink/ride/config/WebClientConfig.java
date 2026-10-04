package com.ridelink.ride.config;

import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import java.time.Duration;

@Configuration
public class WebClientConfig {

    @Value("${driver-service.url}")
    private String driverServiceUrl;

    @Value("${payment-service.url}")
    private String paymentServiceUrl;

    @Bean
    public WebClient driverServiceClient() {
        return WebClient.builder()
            .baseUrl(driverServiceUrl)
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create()
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .responseTimeout(Duration.ofSeconds(10))))
            .build();
    }

    @Bean
    public WebClient paymentServiceClient() {
        return WebClient.builder()
            .baseUrl(paymentServiceUrl)
            .clientConnector(new ReactorClientHttpConnector(
                HttpClient.create()
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .responseTimeout(Duration.ofSeconds(10))))
            .build();
    }
}
