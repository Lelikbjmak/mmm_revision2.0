package com.example.authentication.service;

import com.example.authentication.model.User;

public interface UserService {

    User findByUsername(String username);

    User findByMail(String mail);

    User resetFailedAttempts(User user);

    User unlockUserAfterThreeFailedAttempts(User user);

    User increaseFailedAttempts(User user);
}
