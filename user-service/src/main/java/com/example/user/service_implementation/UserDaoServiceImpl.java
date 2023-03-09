package com.example.user.service_implementation;

import com.example.user.dto.UserDto;
import com.example.user.model.User;
import com.example.user.repository.UserRepository;
import com.example.user.service.UserDaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class UserDaoServiceImpl implements UserDaoService {

    private final UserRepository userRepository;

    @Override
    public UserDto findUserByUsername(String username) {
        return null;
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public UserDto saveUser(User user) {
        // TODO save user
        return null;
    }
}
