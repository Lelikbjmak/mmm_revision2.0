package com.example.authentication.service_implementation;

import com.example.authentication.model.User;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User '" +
                username + "' is not found."));
    }

    @Override
    public User findByMail(String mail) {
        return userRepository.findByMail(mail).orElseThrow(() -> new UsernameNotFoundException("User with mail " +
                mail + " is not found."));
    }

    @Override
    public User resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        return userRepository.save(user);
    }

    @Override
    public User unlockUserAfterThreeFailedAttempts(User user) {

        if (user.getAccountLockTime().isAfter(LocalDateTime.now())) {
            user.setAccountNonLocked(false);
            resetFailedAttempts(user);
        }
        return userRepository.save(user);
    }

    @Override
    public User increaseFailedAttempts(User user) {
        user.setFailedAttempts(user.getFailedAttempts() + 1);
        if (user.getFailedAttempts() == User.MAX_FAILED_ATTEMPTS) {
            user.setAccountNonLocked(false);
            user.setAccountLockTime(LocalDateTime.now());
        }
        return userRepository.save(user);
    }

}
