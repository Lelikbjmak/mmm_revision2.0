package com.example.authentication.service;

import com.example.authentication.dto.AuthenticationRequest;

public interface AuthenticationService {
    String authenticateUser(AuthenticationRequest request);

}
