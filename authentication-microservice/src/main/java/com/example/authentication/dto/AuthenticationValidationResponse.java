package com.example.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationValidationResponse implements Serializable {

    private Date timestamp;

    private String status;

    private int code;

    private boolean isAuthenticated;

    private String username;

    private String authorities;

    private String methodType;

}
