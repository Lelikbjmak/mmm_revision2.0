package com.example.user.dto;

import com.example.user.annotation.ValidUsername;
import com.example.user.model.Role;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class UserDto {

    private Long id;

    @ValidUsername
    private String username;

    private String password;

    private String mail;

    private Set<Role> roles;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean credentialsNonExpired;

    private boolean enabled;

}
