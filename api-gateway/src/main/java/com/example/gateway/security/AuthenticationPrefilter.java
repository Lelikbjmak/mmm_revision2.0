package com.example.gateway.security;

import com.example.gateway.dto.AuthenticationValidationFailedResponse;
import com.example.gateway.dto.AuthenticationValidationSuccessResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.HttpMethod;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Component
@Slf4j
public class AuthenticationPrefilter extends AbstractGatewayFilterFactory<AuthenticationPrefilter.Config> {

    private final WebClient.Builder webClientBuilder;

    @Autowired
    public AuthenticationPrefilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            log.info("**************************************************************************");
            log.info("URL is - " + request.getURI().getPath());
            String bearerToken = Objects.requireNonNull(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION)).substring(7);
            log.info("Bearer Token: " + bearerToken);

            if (isSecured.test(request)) {

                return webClientBuilder.build().get()
                        .uri("http://authentication-microservice/api/auth/validateToken")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + bearerToken)
                        .retrieve().bodyToMono(AuthenticationValidationSuccessResponse.class)
                        .map(response -> {

                            exchange.getRequest().mutate().header("username", response.getUsername());
                            exchange.getRequest().mutate().header("authorities", response.getAuthorities());

                            return exchange;

                        }).flatMap(chain::filter).onErrorResume(error -> {

                            log.info("Error Happened");
                            log.warn(error.getMessage() + "\n" + error.getCause());

                            HttpStatusCode errorCode;
                            String errorMsg;

                            AuthenticationValidationFailedResponse authenticationValidationResponse = null;

                            if (error instanceof WebClientResponseException webClientException) {

                                errorCode = webClientException.getStatusCode();
                                errorMsg = webClientException.getStatusText();

                                System.err.println(errorCode + "   " + errorMsg);

                                authenticationValidationResponse = webClientException.getResponseBodyAs(AuthenticationValidationFailedResponse.class);
                                if (authenticationValidationResponse != null) {
                                    authenticationValidationResponse.setAuthenticated(false);
                                    authenticationValidationResponse.setEndpoint(request.getURI().getPath());
                                }

                            } else {

                                errorCode = HttpStatus.BAD_GATEWAY;
                                errorMsg = HttpStatus.BAD_GATEWAY.getReasonPhrase();

                                authenticationValidationResponse = AuthenticationValidationFailedResponse.builder()
                                        .isAuthenticated(false)
                                        .status(HttpStatus.valueOf(errorCode.value()).name())
                                        .code(errorCode.value())
                                        .timestamp(new Date())
                                        .token(bearerToken)
                                        .endpoint(request.getURI().getPath())
                                        .message(errorMsg)
                                        .build();
                            }

                            return onError(exchange, authenticationValidationResponse);
                        });
            }

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, AuthenticationValidationFailedResponse validationResponse) {

        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
        ServerHttpResponse response = exchange.getResponse();

        response.setStatusCode(HttpStatusCode.valueOf(validationResponse.getCode()));

        try {
            response.getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
            byte[] byteData = objectMapper.writeValueAsBytes(validationResponse);
            return response.writeWith(Mono.just(byteData).map(dataBufferFactory::wrap));

        } catch (JsonProcessingException e) {
            log.error("Error was occurred during writing value to response body after authentication filter. " + e.getMessage());
            return Mono.error(e);
        }
    }

    public static final List<String> openApiEndpoints = List.of(
            "/auth/signIn",
            "/user/free"
    );

    private static final Predicate<ServerHttpRequest> isSecured =
            request -> openApiEndpoints
                    .stream()
                    .noneMatch(uri -> request.getURI().getPath().contains(uri));

    @NoArgsConstructor
    public static class Config {
    }

}
