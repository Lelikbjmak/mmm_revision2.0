package com.example.authentication.service;

import com.example.authentication.model.User;

public interface UserService {

    User findByUsername(String username);

    User findBymMail(String mail);

}
