package com.example.authentication.service;

import com.example.authentication.dto.AuthenticationRequest;
import com.example.authentication.dto.AuthenticationResponse;

public interface AuthenticationService {
    AuthenticationResponse authenticateUser(AuthenticationRequest request);

}
