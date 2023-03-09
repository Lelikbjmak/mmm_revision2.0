package com.example.user.dtomapper;

import com.example.user.dto.UserDto;
import com.example.user.model.User;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserToUserDtoMapper implements Function<User, UserDto> {

    @Override
    public UserDto apply(User user) {

        if (user == null)
            return null;

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .mail(user.getMail())
                .roles(user.getRoles())
                .accountNonExpired(user.isAccountNonExpired())
                .accountNonLocked(user.isAccountNonLocked())
                .credentialsNonExpired(user.isCredentialsNonExpired())
                .enabled(user.isEnabled())
                .build();
    }

}
