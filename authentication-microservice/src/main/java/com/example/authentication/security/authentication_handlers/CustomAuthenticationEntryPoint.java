package com.example.authentication.security.authentication_handlers;

import com.example.authentication.dto.AuthenticationErrorStatus;
import com.example.authentication.dto.AuthenticationFailedResponse;
import com.example.authentication.exceptions.JwtAuthenticationException;
import com.example.authentication.model.User;
import com.example.authentication.service.UserService;
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
import java.util.HashMap;
import java.util.Map;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Component
@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    private final UserService userService;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

        Map<String, Object> additional = new HashMap<>();

        if (authException instanceof JwtAuthenticationException exception) {

            if (exception.getStatus().equals(AuthenticationErrorStatus.USERNAME)) {

                additional.put("username", exception.getUsername());
                additional.put("password", exception.getPassword());

            } else {

                User user = userService.findByUsername(exception.getUsername());

                if (exception.getStatus().equals(AuthenticationErrorStatus.PASSWORD)) {
                    additional.put("attempts to sign in left", User.MAX_FAILED_ATTEMPTS - user.getFailedAttempts());
                }

                if (exception.getStatus().equals(AuthenticationErrorStatus.LOCKED)) {
                    additional.put("locked time", user.getAccountLockTime());
                    additional.put("unlocked time", user.getAccountLockTime().plusDays(1));
                }

                if (exception.getStatus().equals(AuthenticationErrorStatus.EXPIRED)) {
                    additional.put("credentials expired", user.isCredentialsNonExpired());
                    additional.put("account expired", user.isAccountNonExpired());
                }
            }
        }

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        AuthenticationFailedResponse authenticationResponse = AuthenticationFailedResponse.builder()
                .timestamp(new Date())
                .message(authException.getMessage())
                .status(status.name())
                .code(status.value())
                .additional(additional)
                .path(request.getServletPath())
                .build();

        response.setStatus(status.value());
        response.setContentType(APPLICATION_JSON);
        response.getOutputStream().print(objectMapper.writeValueAsString(authenticationResponse));
    }
}
