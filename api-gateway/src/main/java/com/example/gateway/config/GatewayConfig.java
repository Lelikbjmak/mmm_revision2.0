package com.example.gateway.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayConfig {

    @Bean
    @LoadBalanced //it will automatically create WebClient instance to call other multiple instances of microservices
    public WebClient.Builder webClientBuilder(){
        return WebClient.builder();
    }

}
