package com.example.authentication.exceptions;

import lombok.Data;

import java.time.Instant;

@Data
public class InvalidTokenException extends Exception {

    private final String token;
    private final Instant instant;

    private String path = "/api/";
    public InvalidTokenException(String message, String token, Instant instant) {
        super(message);
        this.token = token;
        this.instant = instant;
    }

}
