package com.example.authentication.controller;

import com.example.authentication.dto.AuthenticationRequest;
import com.example.authentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("signIn")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String authenticateUser(@RequestBody AuthenticationRequest authenticationRequest) {
        log.info("User " + authenticationRequest.getUsername() + " is logging...");
        return authenticationService.authenticateUser(authenticationRequest);
    }

}
