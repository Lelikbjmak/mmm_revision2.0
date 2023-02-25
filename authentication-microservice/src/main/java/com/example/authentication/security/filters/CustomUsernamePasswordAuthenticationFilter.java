package com.example.authentication.security.filters;

import com.example.authentication.dto.AuthenticationRequest;
import com.example.authentication.dto.AuthenticationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor
public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;

    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        System.err.println("ffille");
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {

            try {
                AuthenticationRequest authenticationRequest = objectMapper.readValue(request.getInputStream(), AuthenticationRequest.class);

                String username = authenticationRequest.getUsername();
                String password = authenticationRequest.getPassword();

                UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);

                this.setDetails(request, authRequest);
                return this.getAuthenticationManager().authenticate(authRequest);

            } catch (IOException e) {

                HttpStatus status = HttpStatus.CONFLICT;

                AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                        .timestamp(new Date())
                        .token(null)
                        .message("Error processing authentication request. Can't deserialize request body.")
                        .status(status.name())
                        .code(status.value())
                        .build();

                response.setStatus(status.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                try {
                    response.getOutputStream().print(objectMapper.writeValueAsString(authenticationResponse));
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

        }
        return null;
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    private boolean passwordsMatch(String password, String confirmedPassword) {
        return password.equals(confirmedPassword);
    }

}
