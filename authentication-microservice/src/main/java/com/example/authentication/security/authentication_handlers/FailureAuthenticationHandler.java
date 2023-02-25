package com.example.authentication.security.authentication_handlers;

import com.example.authentication.dto.AuthenticationResponse;
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
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Component
@Slf4j
@RequiredArgsConstructor
public class FailureAuthenticationHandler implements AuthenticationFailureHandler {

    private final UserService userService;

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {

        HttpStatus status = HttpStatus.UNAUTHORIZED;

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .timestamp(new Date())
                .token(null)
                .message(exception.getMessage())
                .status(status.name())
                .code(status.value())
                .build();

        String username = (String) request.getAttribute("username");
        if (username != null) {
            User user = userService.findByUsername(username);
            userService.increaseFailedAttempts(user);
            log.info("User {} doesn't pass authentication. Attempts to sign in left: {}", username, User.MAX_FAILED_ATTEMPTS - user.getFailedAttempts());
        }

        response.setStatus(status.value());
        response.setContentType(APPLICATION_JSON);
        response.getOutputStream().print(objectMapper.writeValueAsString(authenticationResponse));
    }

}
