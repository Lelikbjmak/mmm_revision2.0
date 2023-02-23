package com.example.authentication.controller;

import com.example.authentication.dto.AuthenticationRequest;
import com.example.authentication.dto.AuthenticationValidationResponse;
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
    public String authenticateUser(@RequestBody AuthenticationRequest authenticationRequest) {
        log.info("User " + authenticationRequest.getUsername() + " is logging...");
        return authenticationService.authenticateUser(authenticationRequest);
    }

    @GetMapping("validateToken")
    @ResponseStatus(HttpStatus.OK)
    public AuthenticationValidationResponse validateToken(HttpServletRequest request){

        String token = request.getHeader(HttpHeaders.AUTHORIZATION).substring(7);
        String username = jwtService.extractUsername(token);
        userService.findByUsername(username).getAuthorities().forEach(System.out::println);
        System.err.println(token);
        return AuthenticationValidationResponse.builder()
                .isAuthenticated(true)
                .username(username)
                .methodType(HttpMethod.GET)
                .authorities(userService.findByUsername(username).getAuthorities().toString())
                .build();
    }
}
