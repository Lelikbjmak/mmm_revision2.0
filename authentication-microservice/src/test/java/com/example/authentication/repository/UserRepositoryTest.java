package com.example.authentication.repository;

import com.example.authentication.model.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

@DataJpaTest
@TestPropertySource(value = "/application-test-repository.yml")
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName(value = "Context loads")
    public void contextLoadsTest() {
        Assertions.assertNotNull(userRepository);
    }

    @Test
    @DisplayName(value = "Save user 'SUCCESS'")
    public void saveUserSuccessTest(@Value(value = "${user.username}") String username,
                                    @Value(value = "${user.password}") String password,
                                    @Value(value = "${user.mail}") String mail) {
        User user = User.builder()
                .accountLockTime(null)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .accountNonExpired(true)
                .username(username)
                .password(password)
                .mail(mail)
                .build();

        User savedUser = userRepository.save(user);

        Assertions.assertNotNull(savedUser);
        org.assertj.core.api.Assertions.assertThat(savedUser)
                .hasFieldOrPropertyWithValue("username", username);
        org.assertj.core.api.Assertions.assertThat(savedUser)
                .hasFieldOrPropertyWithValue("password", password);
        org.assertj.core.api.Assertions.assertThat(savedUser)
                .hasFieldOrPropertyWithValue("mail", mail);
    }

    @Test
    @DisplayName(value = "Find user 'SUCCESS'")
    @Sql(value = "/sql/authentication-microservice/create-user-before-user-repository-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findUserSuccessTest() {
        final String username = "testUser";
        User findUser = userRepository.findByUsername(username).orElseThrow();
        Assertions.assertNotNull(findUser);
        org.assertj.core.api.Assertions.assertThat(findUser)
                .hasFieldOrPropertyWithValue("username", username);
    }

    @Test
    @DisplayName(value = "Find user 'FAILED'")
    public void findUserFailedTest(@Value(value = "${user.username}") String username,
                                   @Value(value = "${user.mail}") String mail) {

        Exception exception = Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Username not found.")));
        Assertions.assertEquals("Username not found.", exception.getMessage());

        Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userRepository.findByUsername(mail)
                        .orElseThrow(() -> new UsernameNotFoundException("Username not found.")));
        Assertions.assertEquals("Username not found.", exception.getMessage());

    }

    @Test
    @DisplayName(value = "Find user 'SUCCESS'")
    @Sql(value = "/sql/authentication-microservice/create-user-before-user-repository-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void deleteUserSuccessTest() {

        final String username = "testUser";

        System.err.println(username);
        User findUser = userRepository.findByUsername(username).orElseThrow();
        Assertions.assertNotNull(findUser);
        org.assertj.core.api.Assertions.assertThat(findUser)
                .hasFieldOrPropertyWithValue("username", username);
        userRepository.delete(findUser);

        Exception exception = Assertions.assertThrows(UsernameNotFoundException.class,
                () -> userRepository.findByUsername(username)
                        .orElseThrow(() -> new UsernameNotFoundException("User is deleted.")));
        Assertions.assertEquals(exception.getMessage(), "User is deleted.");
    }

    @Test
    @DisplayName(value = "Find all 'SUCCESS'")
    @Sql(value = "/sql/authentication-microservice/create-user-before-user-repository-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findAllUsersSuccessTest() {
        // in sql script insert 4 users
        Assertions.assertEquals(userRepository.findAll().size(), 4);
    }

    @Test
    @DisplayName(value = "Find all (empty table) 'SUCCESS'")
    public void findAllUsersEmpty() {
        Assertions.assertTrue(userRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName(value = "Delete all users 'SUCCESS'")
    @Sql(value = "/sql/authentication-microservice/create-user-before-user-repository-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void deleteAllUsersSuccessTest() {

        List<User> currentUsers = userRepository.findAll();
        // in sql script insert 4 users
        Assertions.assertEquals(currentUsers.size(), 4);

        userRepository.deleteAll();

        Assertions.assertTrue(userRepository.findAll().isEmpty());
    }
}

