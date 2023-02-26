package com.example.authentication.dto;

import lombok.Getter;

@Getter
public enum AuthenticationErrorStatus {

    PASSWORD ("Incorrect password."),
    USERNAME ("Username doesn't exist."),
    ENABLED ("Account isn't enabled."),
    EXPIRED ("Account or Credentials are expired."),
    LOCKED ("Account is locked."),
    ERROR_STATUS("Authentication Error.");

    private final String statusMessage;

    AuthenticationErrorStatus(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
