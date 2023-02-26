package com.example.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationValidationFailedResponse implements Serializable {

    private Date timestamp;

    private int code;

    private String status;

    private String message;

    private String token;

    private boolean isAuthenticated;

    private String endpoint;

}
