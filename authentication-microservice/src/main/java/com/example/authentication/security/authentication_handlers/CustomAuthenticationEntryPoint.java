package com.example.authentication.security.authentication_handlers;

import com.example.authentication.dto.AuthenticationResponse;
import com.example.authentication.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .timestamp(new Date())
                .token(null)
                .message(authException.getMessage())
                .status(status.name())
                .code(status.value())
                .build();

        response.setStatus(status.value());
        response.setContentType(APPLICATION_JSON);
        response.getOutputStream().print(objectMapper.writeValueAsString(authenticationResponse));
    }
}
