package com.example.authentication.service;

import com.example.authentication.model.User;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.service_implementation.UserServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.event.annotation.AfterTestMethod;
import org.springframework.test.context.event.annotation.BeforeTestMethod;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@Transactional(propagation = Propagation.REQUIRED)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private MockitoSession mockitoSession; // to create mockito session (to init all mocks before each method, like restore cache etc... To control some other working aspects).

    @BeforeTestMethod
    public void initSession() {
        mockitoSession = Mockito.mockitoSession()
                .initMocks(this)
                .startMocking();
    }

    @AfterTestMethod
    public void destroySession() {
        mockitoSession.finishMocking();
    }

    @Test
    @DisplayName(value = "Context loads test")
    public void test() {
        Assertions.assertNotNull(userService);
        Assertions.assertNotNull(userRepository);
    }

    @Test
    @DisplayName(value = "Find user by username 'SUCCESS'")
    public void successFindUserByUsernameTest() {

        final String username = "testUser";
        final User user = User.builder()
                .username(username)
                .build();
        Optional<User> optionalUser = Optional.of(user);

        Mockito.when(userRepository.findByUsername(username)).thenReturn(optionalUser);

        User findUser = userService.findByUsername(username);
        Assertions.assertNotNull(findUser);
        org.assertj.core.api.Assertions.assertThat(findUser).hasFieldOrPropertyWithValue("username", username);
    }

    @Test
    @DisplayName(value = "Find user by username 'FAILED'")
    public void failedFindUserByUsernameTest() {

        final String username = "testUser";

        Mockito.when(userRepository.findByUsername(username)).thenThrow(UsernameNotFoundException.class);

        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userService.findByUsername(username));

    }

    @Test
    @DisplayName(value = "Find user by mail 'SUCCESS'")
    public void successFindUserByMailTest() {

        final String mail = "test@gmail.com";
        final User user = User.builder()
                .mail(mail)
                .build();
        Optional<User> optionalUser = Optional.of(user);

        Mockito.when(userRepository.findByMail(mail)).thenReturn(optionalUser);

        User findUser = userService.findByMail(mail);
        Assertions.assertNotNull(findUser);
        org.assertj.core.api.Assertions.assertThat(findUser).hasFieldOrPropertyWithValue("mail", mail);
    }

    @Test
    @DisplayName(value = "Find user by mail 'FAILED'")
    public void failedFindUserByMailTest() {

        final String mail = "test@gmail.com";

        Mockito.when(userRepository.findByMail(mail)).thenThrow(UsernameNotFoundException.class);

        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userService.findByMail(mail));

    }

    @Test
    @DisplayName(value = "Reset failed attempts")
    public void resetFailedAttemptsTest() {

        User user = User.builder()
                .failedAttempts(2)
                .build();

        User resetUser = User.builder()
                .failedAttempts(0)
                .build();

        Mockito.when(userService.resetFailedAttempts(user)).thenReturn(resetUser);
        User updatedUser = userService.resetFailedAttempts(user);

        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(updatedUser.getFailedAttempts(), 0, "Failed attempts were not reset.");
    }

    @Test
    @DisplayName(value = "Unlock user after 3 failed attempts 'Success'")
    public void unlockUserAfterThreeFailedAttemptsSuccessTest() {

        User user = User.builder()
                .failedAttempts(3)
                .accountLockTime(LocalDateTime.now().minusDays(1))
                .accountNonLocked(false)
                .build();

        User returnedUser = User.builder()
                .failedAttempts(0)
                .accountLockTime(null)
                .accountNonLocked(true)
                .build();

        Mockito.when(userService.unlockUserAfterThreeFailedAttempts(user)).thenReturn(returnedUser);

        User unlockedUser = userService.unlockUserAfterThreeFailedAttempts(user);

        Assertions.assertNotNull(unlockedUser);
        Assertions.assertEquals(0, unlockedUser.getFailedAttempts());
        Assertions.assertTrue(unlockedUser.isAccountNonLocked());
        Assertions.assertNull(unlockedUser.getAccountLockTime());
    }

    @Test
    @DisplayName(value = "Unlock user after 3 failed attempts 'Failed'")
    public void unlockUserAfterThreeFailedAttemptsFailedTest() {

        User user = User.builder()
                .failedAttempts(3)
                .accountLockTime(LocalDateTime.now())
                .accountNonLocked(false)
                .build();

        Mockito.when(userService.unlockUserAfterThreeFailedAttempts(user)).thenReturn(user);

        User unlockedUser = userService.unlockUserAfterThreeFailedAttempts(user);

        Assertions.assertNotNull(unlockedUser);
        Assertions.assertEquals(3, unlockedUser.getFailedAttempts());
        Assertions.assertFalse(unlockedUser.isAccountNonLocked());
        Assertions.assertNotNull(unlockedUser.getAccountLockTime());
    }

    @Test
    @DisplayName(value = "Increase failed attempts and lock user")
    public void increaseFailedAttemptsAndLockUserAfterThreeFailedAttemptsTest() {

        User user = User.builder()
                .failedAttempts(2)
                .accountLockTime(null)
                .accountNonLocked(true)
                .build();

        User returnedUser = User.builder()
                .failedAttempts(3)
                .accountLockTime(LocalDateTime.now())
                .accountNonLocked(false)
                .build();

        Mockito.when(userService.increaseFailedAttempts(user)).thenReturn(returnedUser);

        User unlockedUser = userService.increaseFailedAttempts(user);

        Assertions.assertNotNull(unlockedUser);
        Assertions.assertEquals(3, unlockedUser.getFailedAttempts());
        Assertions.assertFalse(unlockedUser.isAccountNonLocked());
        Assertions.assertNotNull(unlockedUser.getAccountLockTime());
    }
}
