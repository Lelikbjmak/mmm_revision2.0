package com.example.user.validator;

import com.example.user.annotation.ValidPassword;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    private final PasswordValidator passwordValidator;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {

        boolean isPasswordValid = passwordValidator.isValid(password);

        if (isPasswordValid) {
            return true;
        } else {
            List<String> errorMessages = passwordValidator.getMessageList();
            String messageTemplate = String.join(",", errorMessages);

            constraintValidatorContext.buildConstraintViolationWithTemplate(messageTemplate)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
            return false;
        }
    }

}
