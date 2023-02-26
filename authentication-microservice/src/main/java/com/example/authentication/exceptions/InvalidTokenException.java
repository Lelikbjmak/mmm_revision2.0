package com.example.authentication.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
public class InvalidTokenException extends Exception {

    private final String token;

    private final Instant instant;

    @Setter
    private String path = "/api/";

    public InvalidTokenException(String message, String token, Instant instant) {
        super(message);
        this.token = token;
        this.instant = instant;
    }

}
