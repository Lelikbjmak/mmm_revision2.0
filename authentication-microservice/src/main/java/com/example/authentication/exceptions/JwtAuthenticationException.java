package com.example.authentication.exceptions;

import com.example.authentication.dto.AuthenticationErrorStatus;
import lombok.Getter;
import org.springframework.security.core.AuthenticationException;

@Getter
public class JwtAuthenticationException extends AuthenticationException {

    private final String username;

    private final String password;

    private final AuthenticationErrorStatus status;

    public JwtAuthenticationException(String msg, String username, String password, AuthenticationErrorStatus status) {
        super(msg);
        this.username = username;
        this.password = password;
        this.status = status;
    }

}
