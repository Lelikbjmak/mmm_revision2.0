package com.example.user.service;

import com.example.user.dto.UserDto;
import com.example.user.model.User;

public interface UserDaoService {

    UserDto findUserByUsername(String username);

    boolean existsByUsername(String username);

    UserDto saveUser(User user);
}
