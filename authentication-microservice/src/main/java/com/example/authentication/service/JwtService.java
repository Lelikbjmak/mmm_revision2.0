package com.example.authentication.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

public abstract class JwtService {

    private static final String SECRET_KEY = "2B4D6251655468576D5A7134743777217A25432A462D4A404E635266556A586E";
    protected static final long JWT_TOKEN_VALIDITY = 1000 * 60 * 60 * 2;  // 2 hours
    public static final String JWT_COOKIE_NAME = "token";
    private static final int JWT_COOKIE_MAX_AGE_SECONDS = 120;
    private static final int MAX_REFRESH_WINDOW_SECONDS = 30;
    public abstract String generateToken(UserDetails userDetails);

    public String extractUsername(String jwtToken) throws MalformedJwtException {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String jwtToken) {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey()) // digital key to proof that the sender of JWT is who it claims to be + ensure that the message wasn't changed all the way
                    .build()
                    .parseClaimsJws(jwtToken)
                    .getBody();
    }

    protected static Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public abstract boolean isJwtTokenValid(String jwtToken, UserDetails userDetails);

    protected Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

    public boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

}
