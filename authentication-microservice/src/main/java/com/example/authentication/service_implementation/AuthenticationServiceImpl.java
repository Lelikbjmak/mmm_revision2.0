package com.example.authentication.service_implementation;

import com.example.authentication.dto.AuthenticationErrorStatus;
import com.example.authentication.dto.AuthenticationRequest;
import com.example.authentication.dto.AuthenticationResponse;
import com.example.authentication.exceptions.JwtAuthenticationException;
import com.example.authentication.model.User;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.service.AuthenticationService;
import com.example.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtServiceImpl jwtService;

    private final UserRepository userRepository;

    private final Argon2PasswordEncoder passwordEncoder;

    private final UserService userService;

    @Override
    public AuthenticationResponse authenticateUser(AuthenticationRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {

            if (userRepository.findByUsername(request.getUsername()).isEmpty()) {
                AuthenticationErrorStatus status = AuthenticationErrorStatus.USERNAME;
                throw new JwtAuthenticationException(status.getStatusMessage(), request.getUsername(), request.getPassword(), status);
            } else {

                User user = userService.findByUsername(request.getUsername());
                AuthenticationErrorStatus status;

                if (!user.isEnabled()) {
                    status = AuthenticationErrorStatus.ENABLED;

                } else if (!user.isAccountNonExpired() | !user.isCredentialsNonExpired()) {
                    status = AuthenticationErrorStatus.EXPIRED;

                } else if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                    status = AuthenticationErrorStatus.PASSWORD;
                    userService.increaseFailedAttempts(user);
                    if (!user.isAccountNonLocked())
                        status = AuthenticationErrorStatus.LOCKED;

                } else if (!user.isAccountNonLocked()) {
                    status = AuthenticationErrorStatus.LOCKED;

                } else {
                    status = AuthenticationErrorStatus.ERROR_STATUS;
                }

                throw new JwtAuthenticationException(status.getStatusMessage(), request.getUsername(), request.getPassword(), status);

            }
        }

        HttpStatus status = HttpStatus.ACCEPTED;

        User user = userService.findByUsername(request.getUsername());
        String token = jwtService.generateToken(user);

        return AuthenticationResponse.builder()
                .code(status.value())
                .status(status.name())
                .token(token)
                .timestamp(new Date())
                .message("Successfully authenticated.")
                .build();
    }

}
