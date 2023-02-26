package com.example.authentication.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationFailedResponse {

    private Date timestamp;

    private int code;

    private String status;

    private String message;

    private String path;

    private Map<String, Object> additional;
}
