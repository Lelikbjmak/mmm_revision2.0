package com.example.authentication.controller;

import com.example.authentication.AuthenticationMicroserviceApplication;
import com.example.authentication.dto.AuthenticationFailedResponse;
import com.example.authentication.dto.AuthenticationResponse;
import com.example.authentication.dto.AuthenticationValidationResponse;
import com.example.authentication.model.User;
import com.example.authentication.service.JwtService;
import com.example.authentication.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = {AuthenticationMicroserviceApplication.class})
@AutoConfigureMockMvc
@ActiveProfiles({"test-controllers"})
public class AuthenticationControllerTest {

    private final MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    private final AuthenticationController authenticationController;

    private final UserService userService;

    private final JwtService jwtService;

    @Autowired
    AuthenticationControllerTest(MockMvc mockMvc, ObjectMapper objectMapper, AuthenticationController authenticationController, UserService userService, JwtService jwtService) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.authenticationController = authenticationController;
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @Test
    @DisplayName("Context loads")
    public void contextLoads() {
        Assertions.assertNotNull(authenticationController, "Authentication controller is null.");
    }

    @Test
    @DisplayName(value = "Authentication 'success'")
    @Sql(value = "/sql/authentication-microservice/create-users-before-authentication.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/authentication-microservice/drop-users-after-authentication.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void successAuthenticationTest(@Value(value = "${user[0].username}") String username,
                                          @Value(value = "${user[0].password}") String password) throws Exception {

        String authenticationRequest = "{\"username\":\"" + username + "\"," +
                " \"password\":\"" + password + "\"}";

        ResultActions resultActions = mockMvc.perform(post("/api/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticationRequest))
                .andDo(print())
                .andExpect(status().isAccepted());

        MvcResult result = resultActions.andReturn();
        String stringResponse = result.getResponse().getContentAsString();
        AuthenticationResponse authenticationResponse = objectMapper.readValue(stringResponse, AuthenticationResponse.class);

        Assertions.assertNotNull(authenticationResponse, "Response can't be deserialized to AuthenticationResponse.class.");
        Assertions.assertNotNull(authenticationResponse.getToken(), "Token is null.");
    }

    @Test
    @DisplayName(value = "Authentication 'FAILED'. User doesn't exist")
    public void failedAuthenticationTestUserNotExist(@Value(value = "${user[4].username}") String username,
                                                     @Value(value = "${user[4].password}") String password,
                                                     @Value(value = "${response.error.message.user_not_exists}") String errorMessage) throws Exception {

        String authenticationRequest = "{\"username\":\"" + username + "\"," +
                " \"password\":\"" + password + "\"}";

        ResultActions resultActions = mockMvc.perform(post("/api/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticationRequest))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        MvcResult result = resultActions.andReturn();
        String stringResponse = result.getResponse().getContentAsString();
        AuthenticationFailedResponse authenticationResponse = objectMapper.readValue(stringResponse, AuthenticationFailedResponse.class);

        Assertions.assertNotNull(authenticationResponse, "Response can't be deserialized to AuthenticationResponse.class.");
        Assertions.assertEquals(authenticationResponse.getCode(), HttpStatus.UNAUTHORIZED.value());
        Assertions.assertEquals(errorMessage, authenticationResponse.getMessage());
    }

    @Test
    @DisplayName(value = "Authentication 'FAILED'. User expired")
    @Sql(value = "/sql/authentication-microservice/create-users-before-authentication.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/authentication-microservice/drop-users-after-authentication.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedAuthenticationTestUserExpired(@Value(value = "${user[3].username}") String username,
                                                    @Value(value = "${user[3].password}") String password,
                                                    @Value(value = "${response.error.message.user_expired}") String errorMessage) throws Exception {

        String authenticationRequest = "{\"username\":\"" + username + "\"," +
                " \"password\":\"" + password + "\"}";

        ResultActions resultActions = mockMvc.perform(post("/api/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticationRequest))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        MvcResult result = resultActions.andReturn();
        String stringResponse = result.getResponse().getContentAsString();
        AuthenticationFailedResponse authenticationResponse = objectMapper.readValue(stringResponse, AuthenticationFailedResponse.class);

        Assertions.assertNotNull(authenticationResponse, "Response can't be deserialized to AuthenticationResponse.class.");
        Assertions.assertEquals(authenticationResponse.getCode(), HttpStatus.UNAUTHORIZED.value());
        Assertions.assertEquals(errorMessage, authenticationResponse.getMessage());
    }

    @Test
    @DisplayName(value = "Authentication 'FAILED'. User expired")
    @Sql(value = "/sql/authentication-microservice/create-users-before-authentication.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/authentication-microservice/drop-users-after-authentication.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedAuthenticationTestUserDisabled(@Value(value = "${user[2].username}") String username,
                                                     @Value(value = "${user[2].password}") String password,
                                                     @Value(value = "${response.error.message.user_disabled}") String errorMessage) throws Exception {

        String authenticationRequest = "{\"username\":\"" + username + "\"," +
                " \"password\":\"" + password + "\"}";

        ResultActions resultActions = mockMvc.perform(post("/api/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticationRequest))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        MvcResult result = resultActions.andReturn();
        String stringResponse = result.getResponse().getContentAsString();
        AuthenticationFailedResponse authenticationResponse = objectMapper.readValue(stringResponse, AuthenticationFailedResponse.class);

        Assertions.assertNotNull(authenticationResponse, "Response can't be deserialized to AuthenticationResponse.class.");
        Assertions.assertEquals(authenticationResponse.getCode(), HttpStatus.UNAUTHORIZED.value());
        Assertions.assertEquals(errorMessage, authenticationResponse.getMessage());
    }

    @Test
    @DisplayName(value = "Authentication 'FAILED'. User expired")
    @Sql(value = "/sql/authentication-microservice/create-users-before-authentication.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/authentication-microservice/drop-users-after-authentication.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedAuthenticationTestUserLocked(@Value(value = "${user[1].username}") String username,
                                                   @Value(value = "${user[1].password}") String password,
                                                   @Value(value = "${response.error.message.user_locked}") String errorMessage) throws Exception {

        String authenticationRequest = "{\"username\":\"" + username + "\"," +
                " \"password\":\"" + password + "\"}";

        ResultActions resultActions = mockMvc.perform(post("/api/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticationRequest))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        MvcResult result = resultActions.andReturn();
        String stringResponse = result.getResponse().getContentAsString();
        AuthenticationFailedResponse authenticationResponse = objectMapper.readValue(stringResponse, AuthenticationFailedResponse.class);

        Assertions.assertNotNull(authenticationResponse, "Response can't be deserialized to AuthenticationResponse.class.");
        Assertions.assertEquals(authenticationResponse.getCode(), HttpStatus.UNAUTHORIZED.value());
        Assertions.assertEquals(errorMessage, authenticationResponse.getMessage());
    }

    @Test
    @DisplayName(value = "Authentication 'FAILED'. User expired")
    @Sql(value = "/sql/authentication-microservice/create-users-before-authentication.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/authentication-microservice/drop-users-after-authentication.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedAuthenticationTestUserIncorrectPassword(@Value(value = "${user[0].username}") String username,
                                                              @Value(value = "${user[0].incorrect_password}") String password,
                                                              @Value(value = "${response.error.message.incorrect_password}") String errorMessage) throws Exception {

        String authenticationRequest = "{\"username\":\"" + username + "\"," +
                " \"password\":\"" + password + "\"}";

        ResultActions resultActions = mockMvc.perform(post("/api/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticationRequest))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        MvcResult result = resultActions.andReturn();
        String stringResponse = result.getResponse().getContentAsString();
        AuthenticationFailedResponse authenticationResponse = objectMapper.readValue(stringResponse, AuthenticationFailedResponse.class);

        Assertions.assertNotNull(authenticationResponse, "Response can't be deserialized to AuthenticationResponse.class.");
        Assertions.assertEquals(authenticationResponse.getCode(), HttpStatus.UNAUTHORIZED.value());
        Assertions.assertEquals(errorMessage, authenticationResponse.getMessage());
    }

    @Test
    @DisplayName(value = "Authentication 'FAILED'. User expired after 3 failed attempts to sign in")
    @Sql(value = "/sql/authentication-microservice/create-users-before-authentication.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/authentication-microservice/drop-users-after-authentication.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedAuthenticationTestUserLockedAfterThreeFailedAttempts(@Value(value = "${user[0].username}") String username,
                                                                           @Value(value = "${user[0].incorrect_password}") String password,
                                                                           @Value(value = "${response.error.message.incorrect_password}") String errorPasswordMessage,
                                                                           @Value(value = "${response.error.message.user_locked}") String errorLockedMessage) throws Exception {
        final String keyForFailedAttempts = "attempts to sign in left";
        final String keyForUserLockedTime = "locked time";
        final String keyForUserUnlockedTime = "unlocked time";

        User user = userService.findByUsername(username);
        Assertions.assertNotNull(user);

        String authenticationRequest = "{\"username\":\"" + username + "\"," +
                " \"password\":\"" + password + "\"}";

        // 1st attempt
        ResultActions resultActions = mockMvc.perform(post("/api/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticationRequest))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        MvcResult result = resultActions.andReturn();
        String stringResponse = result.getResponse().getContentAsString();
        AuthenticationFailedResponse authenticationResponse = objectMapper.readValue(stringResponse, AuthenticationFailedResponse.class);

        Assertions.assertNotNull(authenticationResponse, "Response can't be deserialized to AuthenticationResponse.class.");
        Assertions.assertEquals(authenticationResponse.getCode(), HttpStatus.UNAUTHORIZED.value());
        Assertions.assertEquals(errorPasswordMessage, authenticationResponse.getMessage());
        Assertions.assertEquals(authenticationResponse.getAdditional().get(keyForFailedAttempts), 2, "Attempts to sing in were not reduced.");

        user = userService.findByUsername(username);
        Assertions.assertEquals(user.getFailedAttempts(), 1, "Attempts to sing in were not reduced.");

        // 2nd attempt
        resultActions = mockMvc.perform(post("/api/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticationRequest))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        result = resultActions.andReturn();
        stringResponse = result.getResponse().getContentAsString();
        authenticationResponse = objectMapper.readValue(stringResponse, AuthenticationFailedResponse.class);

        Assertions.assertNotNull(authenticationResponse, "Response can't be deserialized to AuthenticationResponse.class.");
        Assertions.assertEquals(authenticationResponse.getCode(), HttpStatus.UNAUTHORIZED.value());
        Assertions.assertEquals(errorPasswordMessage, authenticationResponse.getMessage());
        Assertions.assertEquals(authenticationResponse.getAdditional().get(keyForFailedAttempts), 1, "Attempts to sing in were not reduced.");

        user = userService.findByUsername(username);
        Assertions.assertEquals(user.getFailedAttempts(), 2, "Attempts to sing in were not reduced.");

        // 3rd attempt -> user will be locked
        resultActions = mockMvc.perform(post("/api/auth/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticationRequest))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        result = resultActions.andReturn();
        stringResponse = result.getResponse().getContentAsString();
        authenticationResponse = objectMapper.readValue(stringResponse, AuthenticationFailedResponse.class);

        Assertions.assertNotNull(authenticationResponse, "Response can't be deserialized to AuthenticationResponse.class.");
        Assertions.assertEquals(authenticationResponse.getCode(), HttpStatus.UNAUTHORIZED.value());
        Assertions.assertEquals(errorLockedMessage, authenticationResponse.getMessage());
        authenticationResponse.getAdditional().get(keyForUserLockedTime);

        LocalDateTime lockDate = LocalDateTime
                .parse((CharSequence) authenticationResponse
                        .getAdditional()
                        .get(keyForUserLockedTime));

        LocalDateTime unlockDate = LocalDateTime
                .parse((CharSequence) authenticationResponse
                        .getAdditional()
                        .get(keyForUserUnlockedTime));

        Assertions.assertTrue(lockDate.isBefore(LocalDateTime.now()), "Expired date is the date from future.");
        Assertions.assertTrue(unlockDate.isAfter(LocalDateTime.now()), "Unlock date is the date from the past.");

        user = userService.findByUsername(username);
        Assertions.assertEquals(user.getFailedAttempts(), 3, "Attempts to sing in were not reduced.");
        Assertions.assertFalse(user.isAccountNonLocked());
    }

    @Test
    @DisplayName(value = "Token validation 'SUCCESS'")
    @Sql(value = "/sql/authentication-microservice/create-users-before-authentication.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/authentication-microservice/drop-users-after-authentication.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void successTokenValidationTest(@Value(value = "${user[0].username}") String username) throws Exception {

        User user = userService.findByUsername(username);
        String token = "Bearer " + jwtService.generateToken(user);

        ResultActions resultActions = mockMvc.perform(get("/api/auth/validateToken")
                .header(HttpHeaders.AUTHORIZATION, token))
                .andDo(print())
                .andExpect(status().isAccepted());

        MvcResult result = resultActions.andReturn();
        String responseString = result.getResponse().getContentAsString();
        AuthenticationValidationResponse response = objectMapper.readValue(responseString, AuthenticationValidationResponse.class);

        Assertions.assertEquals(response.getUsername(), username, "Username doesn't match.");
    }

    @Test
    @DisplayName(value = "Token validation 'FAILED' incorrect token format.")
    @Sql(value = "/sql/authentication-microservice/create-users-before-authentication.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/authentication-microservice/drop-users-after-authentication.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedTokenValidationTestIncorrectTokenFormat(@Value(value = "${user[0].username}") String username) throws Exception {

        User user = userService.findByUsername(username);
        String token = "Bearer " + jwtService.generateToken(user);

        Assertions.assertTrue(mockMvc.perform(get("/api/auth/validateToken")
                        .header(HttpHeaders.AUTHORIZATION, token.substring(0, token.length() - 3)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains("JWT signature does not match"));
    }

    @Test
    @DisplayName(value = "Token validation 'FAILED' token is not present.")
    public void failedTokenValidationTestUnauthorizedRequest() throws Exception {

        Assertions.assertTrue(mockMvc.perform(get("/api/auth/validateToken"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains("Full authentication is required to access"));

    }

    @Test
    @DisplayName(value = "Token validation 'FAILED' incorrect token format.")
    @Sql(value = "/sql/authentication-microservice/create-users-before-authentication.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(value = "/sql/authentication-microservice/drop-users-after-authentication.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    public void failedTokenValidationTestTokenExpired(@Value(value = "${user[0].username}") String username,
                                                      @Value(value = "${token.secret}") String secretKey) throws Exception {

        User user = userService.findByUsername(username);
        String token = "Bearer " + Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis() - 2000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(getSignInKey(secretKey), SignatureAlgorithm.HS256)
                .compact();

        Assertions.assertTrue(mockMvc.perform(get("/api/auth/validateToken")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .contains("JWT expired at"));
    }

    private static Key getSignInKey(String secret){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
