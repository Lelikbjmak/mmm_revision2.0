package com.example.authentication.service_implementation;

import com.example.authentication.exceptions.InvalidTokenException;
import com.example.authentication.service.JwtService;
import io.jsonwebtoken.*;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;

@Service
@NoArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class JwtServiceImpl extends JwtService {

    @Override
    public String generateToken(UserDetails userDetails) {

        return Jwts
                .builder()
                .setClaims(new HashMap<>())
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))   // 2 hours
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public boolean isJwtTokenValid(String jwtToken, UserDetails userDetails) throws MalformedJwtException, ExpiredJwtException, UnsupportedJwtException, IllegalArgumentException, InvalidTokenException {
        final String username = extractUsername(jwtToken);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(jwtToken);
    }

    @Override
    public boolean isTokenExpired(String jwtToken) throws InvalidTokenException {
        return extractExpiration(jwtToken).before(new Date());
    }

}
