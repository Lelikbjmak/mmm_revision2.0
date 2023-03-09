package com.example.user.dto;

import com.example.user.annotation.ValidUsername;
import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
// TODO: Validations for password, confirmedPassword(equals password), mail(unique)
public class registrationUserDto {

    @ValidUsername
    private String username;

    private String password;

    private String confirmedPassword;

    @Email
    private String mail;

}
