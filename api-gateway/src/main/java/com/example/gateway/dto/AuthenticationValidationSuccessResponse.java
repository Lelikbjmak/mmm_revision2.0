package com.example.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationValidationSuccessResponse {

    private Date timestamp;

    private String status;

    private int code;

    private boolean isAuthenticated;

    private String username;

    private String authorities;

    private String methodType;

}
