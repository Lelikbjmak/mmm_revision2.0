package com.example.authentication.service_implementation;

import com.example.authentication.model.User;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(RuntimeException::new);
    }

    @Override
    public User findBymMail(String mail) {
        return userRepository.findByUsername(mail).orElseThrow(RuntimeException::new);
    }

}
