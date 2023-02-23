package com.example.user.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@NoArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Auth header is null, or doesn't contain Bearer token.");
            filterChain.doFilter(request, response);
            return;
        }

        logHeaders(request);

        String username = request.getHeader("username");
        List<Map<String, String>> authorities = new ArrayList<>();

        String authoritiesStr = request.getHeader("authorities");
        System.err.println(username + "\n" + authoritiesStr);
        Set<SimpleGrantedAuthority> simpleGrantedAuthorities = new HashSet<>();

        simpleGrantedAuthorities = Arrays
                .stream(authoritiesStr.split(","))
                .distinct()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, simpleGrantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private void logHeaders(HttpServletRequest httpServletRequest) {
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while(headerNames.hasMoreElements()) {
            String header=headerNames.nextElement();
            logger.info(String.format("Header: %s --- Value: %s", header, httpServletRequest.getHeader(header)));

        }
    }
}
