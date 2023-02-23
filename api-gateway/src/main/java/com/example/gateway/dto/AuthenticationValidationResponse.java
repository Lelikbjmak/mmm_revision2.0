package com.example.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationValidationResponse implements Serializable {
    private String status;
    private boolean isAuthenticated;
    private String methodType;
    private String username;
    private String authorities;
}
