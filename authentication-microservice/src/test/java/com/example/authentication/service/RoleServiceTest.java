package com.example.authentication.service;

import com.example.authentication.model.EnumRole;
import com.example.authentication.model.Role;
import com.example.authentication.repository.RoleRepository;
import com.example.authentication.service_implementation.RoleServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@Transactional(propagation = Propagation.REQUIRED)
public class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    @DisplayName(value = "Context loads")
    public void contextLoadTest() {
        Assertions.assertNotNull(roleRepository);
        Assertions.assertNotNull(roleService);
    }

    @Test
    @DisplayName(value = "Find by role 'SUCCESS'")
    public void findByRoleSuccessTest() {
        Mockito.when(roleRepository.findByRole(EnumRole.ROLE_USER))
                .thenReturn(Optional.of(Role.builder()
                        .role(EnumRole.ROLE_USER)
                        .build()));

        Role role = roleService.findByRole(EnumRole.ROLE_USER);
        org.assertj.core.api.Assertions.assertThat(role)
                .hasFieldOrPropertyWithValue("role", EnumRole.ROLE_USER);
    }

    @Test
    @DisplayName(value = "Find by role 'FAILED'")
    public void findByRoleFailedTest() {

        Mockito.when(roleRepository.findByRole(EnumRole.ROLE_USER))
                .thenReturn(Optional.of(Role.builder()
                        .role(EnumRole.ROLE_USER)
                        .build()));

        Assertions.assertThrows(RuntimeException.class,
                () -> roleService.findByRole(EnumRole.ROLE_ADMIN));
    }
}
