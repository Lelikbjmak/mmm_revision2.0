package com.example.authentication.controller;

import com.example.authentication.dto.AuthenticationRequest;
import com.example.authentication.dto.AuthenticationResponse;
import com.example.authentication.dto.AuthenticationValidationResponse;
import com.example.authentication.exceptions.InvalidTokenException;
import com.example.authentication.service.AuthenticationService;
import com.example.authentication.service.JwtService;
import com.example.authentication.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.HttpMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    private final JwtService jwtService;

    private final UserService userService;

    @PostMapping("signIn")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AuthenticationResponse authenticateUser(@RequestBody AuthenticationRequest authenticationRequest, HttpServletRequest request) {
        log.info("User " + authenticationRequest.getUsername() + " is logging...");
        return authenticationService.authenticateUser(authenticationRequest);
    }

    @GetMapping("validateToken")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public AuthenticationValidationResponse validateToken(HttpServletRequest request) throws InvalidTokenException {

        String token = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String username = jwtService.extractUsername(token);

        return AuthenticationValidationResponse.builder()
                .isAuthenticated(true)
                .username(username)
                .methodType(HttpMethod.GET)
                .authorities(userService.findByUsername(username).getAuthorities().toString())
                .code(HttpStatus.OK.value())
                .status(HttpStatus.OK.name())
                .timestamp(new Date())
                .build();
    }
}
