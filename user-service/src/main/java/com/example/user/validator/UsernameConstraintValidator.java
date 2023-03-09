package com.example.user.validator;

import com.example.user.annotation.ValidUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class UsernameConstraintValidator implements ConstraintValidator<ValidUsername, String> {

    private final UsernameValidator usernameValidator;

    @Override
    public boolean isValid(String username, ConstraintValidatorContext constraintValidatorContext) {

        boolean isUsernameValid = usernameValidator.isValid(username);
        if (isUsernameValid)
            return true;
        else {
            System.err.println("FALSE");
            List<String> errorMessages = usernameValidator.getMessages();
            usernameValidator.getMessages().forEach(System.err::println);
            String messageTemplate = String.join(",", errorMessages);

            constraintValidatorContext.buildConstraintViolationWithTemplate(messageTemplate)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();

            return false;
        }
    }
}
