package com.example.authentication.repository;

import com.example.authentication.model.EnumRole;
import com.example.authentication.model.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@TestPropertySource(value = "/application-test-repository.yml")
public class RoleRepositoryTest {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleRepositoryTest(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Test
    @DisplayName(value = "Context loads")
    public void contextLoadTest() {
        Assertions.assertNotNull(roleRepository);
    }

    @Test
    @DisplayName(value = "Save role 'SUCCESS'")
    public void saveRoleSuccessTest() {

        Role role = roleRepository.save(Role.builder()
                .role(EnumRole.ROLE_USER)
                .build());

        Assertions.assertNotNull(role);
    }

    @Test
    @DisplayName(value = "Find by role 'SUCCESS'")
    @Sql(value = "/sql/authentication-microservice/create-roles-before-role-repository-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findByRoleSuccessTest() {
        final EnumRole enumRole = EnumRole.ROLE_USER;
        Optional<Role> role = roleRepository.findByRole(enumRole);
        Assertions.assertFalse(role.isEmpty());
        org.assertj.core.api.Assertions.assertThat(role.orElseThrow()).hasFieldOrPropertyWithValue("role", enumRole);
    }

    @Test
    @DisplayName(value = "Find by role 'FAILED'")
    @Sql(value = "/sql/authentication-microservice/create-roles-before-role-repository-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findByRoleFailedTest() {
        final EnumRole enumRole = EnumRole.ROLE_MODERATOR;
        Optional<Role> role = roleRepository.findByRole(enumRole);
        Assertions.assertTrue(role.isEmpty());
        Assertions.assertThrows(RuntimeException.class,
                () -> roleRepository
                        .findByRole(enumRole)
                        .orElseThrow(RuntimeException::new));
    }

    @Test
    @DisplayName(value = "Find all roles, empty roleList 'SUCCESS'")
    public void findAllEmptySuccessTest() {
        List<Role> roles = roleRepository.findAll();
        Assertions.assertEquals(roles, new ArrayList<>());
    }

    @Test
    @DisplayName(value = "Find all roles 'SUCCESS'")
    @Sql(value = "/sql/authentication-microservice/create-roles-before-role-repository-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void findAllSuccessTest() {
        final int size = 2;
        List<Role> roles = roleRepository.findAll();
        Assertions.assertEquals(roles.size(), size);
    }

    @Test
    @DisplayName(value = "Delete by role 'SUCCESS'")
    @Sql(value = "/sql/authentication-microservice/create-roles-before-role-repository-test.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    public void deleteByRoleSuccessTest() {
        final EnumRole enumRole = EnumRole.ROLE_USER;
        Optional<Role> role = roleRepository.findByRole(enumRole);
        Assertions.assertFalse(role.isEmpty());
        org.assertj.core.api.Assertions.assertThat(role.orElseThrow()).hasFieldOrPropertyWithValue("role", enumRole);

        roleRepository.delete(role.orElseThrow());

        Assertions.assertTrue(roleRepository.
                findByRole(enumRole).isEmpty());
    }
}
