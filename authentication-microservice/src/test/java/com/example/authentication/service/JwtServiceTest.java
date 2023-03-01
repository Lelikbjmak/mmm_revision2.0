package com.example.authentication.service;

import com.example.authentication.exceptions.InvalidTokenException;
import com.example.authentication.model.User;
import com.example.authentication.service_implementation.JwtServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.HashMap;
import java.util.function.Supplier;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private JwtServiceImpl jwtServiceImpl;

    private static final long JWT_TOKEN_VALIDITY = 1000L * 60 * 60 * 2;

    private static final String SECRET_KEY = "2B4D6251655468576D5A7134743777217A25432A462D4A404E635266556A586E";

    @Test
    @DisplayName(value = "Context loads")
    public void contextLoadsTest() {
        Assertions.assertNotNull(jwtService);
        Assertions.assertNotNull(jwtServiceImpl);
    }

    @Test
    @DisplayName(value = "Generate token")
    public void generateToken() {
        User user = User.builder().username("user").build();

        String token = jwtServiceImpl.generateToken(user);
        Assertions.assertNotNull(token);
    }

    @Test
    @DisplayName(value = "Extract username")
    public void extractUsernameTest() {

        User user = User.builder().username("user").build();

        String token = jwtServiceImpl.generateToken(user);

        try {
            String username = jwtServiceImpl.extractUsername(token);
            Assertions.assertEquals(username, user.getUsername());
        } catch (InvalidTokenException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName(value = "Extract expire date")
    public void extractExpireDateTest() {

        String token = jwtServiceImpl.generateToken(new User());

        try {
            Date expireDate = jwtServiceImpl.extractExpiration(token);
            Assertions.assertNotNull(expireDate);

            Assertions.assertTrue(expireDate.after(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY - 1000 * 5)));  // 5 seconds diff approximately

            Assertions.assertTrue(expireDate.before(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY)));

        } catch (InvalidTokenException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName(value = "Token is valid 'VALID'")
    public void validateTokenSuccessTest() {

        User user = User.builder().username("user").build();

        String token = jwtServiceImpl.generateToken(user);

        try {
            boolean isValid = jwtServiceImpl.isJwtTokenValid(token, user);
            Assertions.assertTrue(isValid);
        } catch (InvalidTokenException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName(value = "Token is valid 'INVALID', incorrect username")
    public void validateTokenFailedUsernameTest() {

        User user = User.builder().username("user").build();

        String token = jwtServiceImpl.generateToken(user);

        try {
            boolean isValid = jwtServiceImpl.isJwtTokenValid(token, new User()); // incorrect username
            Assertions.assertFalse(isValid);
        } catch (InvalidTokenException e) {
            System.err.println(e.getMessage());
        }
    }


    @Test
    @DisplayName(value = "Token is valid 'INVALID', token expired")
    public void validateTokenFailedExpirationTest() {

        User user = User.builder().username("user").build();

        String token = getExpiredToken.get();

        Exception exception = Assertions.assertThrows(InvalidTokenException.class,
                () -> jwtServiceImpl.isJwtTokenValid(token, user)); // token expired

        org.assertj.core.api.Assertions.assertThat(exception.getMessage()).contains("JWT expired at");
    }

    @Test
    @DisplayName(value = "Token is valid 'INVALID', token is null/empty")
    public void validateTokenIsNullTest() {

        User user = User.builder().username("user").build();

        Exception exception = Assertions.assertThrows(InvalidTokenException.class,
                () -> jwtServiceImpl.isJwtTokenValid(null, user));

        org.assertj.core.api.Assertions.assertThat(exception.getMessage()).contains("JWT String argument");
    }

    @Test
    @DisplayName(value = "Token is valid 'INVALID', malformed exception")
    public void validateTokenIsMalformedTest() {

        User user = User.builder().username("user").build();

        String token = jwtServiceImpl.generateToken(user);

        Exception exception = Assertions.assertThrows(InvalidTokenException.class,
                () -> jwtServiceImpl.isJwtTokenValid(token.substring(2), user));

        org.assertj.core.api.Assertions.assertThat(exception.getMessage()).contains("Malformed");
    }

    @Test
    @DisplayName(value = "Token is valid 'INVALID', signature exception")
    public void validateTokenSignatureErrorTest() {

        User user = User.builder().username("user").build();

        String token = jwtServiceImpl.generateToken(user);

        Exception exception = Assertions.assertThrows(InvalidTokenException.class,
                () -> jwtServiceImpl.isJwtTokenValid(token.substring(0, token.length() - 2), user));

        org.assertj.core.api.Assertions.assertThat(exception.getMessage()).contains("JWT signature");
    }

    @Test
    @DisplayName(value = "Token is valid 'INVALID', UnsupportedJwt exception")
    public void validateTokenSTest() {

        User user = User.builder().username("user").build();

        Exception exception = Assertions.assertThrows(InvalidTokenException.class,
                () -> jwtServiceImpl.isJwtTokenValid(SECRET_KEY, user));

        org.assertj.core.api.Assertions.assertThat(exception.getMessage()).contains("JWT strings must");
    }

    @Test
    @DisplayName(value = "Expired token")
    public void isTokenExpired() {

        User user = User.builder().username("user").build();
        String token = jwtServiceImpl.generateToken(user);
        String expiredToken = getExpiredToken.get();

        try {
            boolean isExpired = jwtServiceImpl.isTokenExpired(token);
            Assertions.assertFalse(isExpired);

            Assertions.assertThrows(InvalidTokenException.class,
                    () -> Assertions.assertTrue(jwtServiceImpl.isTokenExpired(expiredToken)));

        } catch (InvalidTokenException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Supplier<String> getExpiredToken = () -> Jwts.builder().
            setClaims(new HashMap<>())
            .setSubject(User.builder()
                    .username("user")
                    .build().getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis() + 1000))
            .setExpiration(new Date(System.currentTimeMillis()))   // expire it after 1 second
            .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY)),
                    SignatureAlgorithm.HS256)
            .compact();
}
