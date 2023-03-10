package com.example.user.validator;

import com.example.user.annotation.ValidEmail;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class EmailConstraintValidator implements ConstraintValidator<ValidEmail, String> {

    private final MailValidator mailValidator;

    @Override
    public boolean isValid(String validatingMail, ConstraintValidatorContext constraintValidatorContext) {

        boolean isMailValid = mailValidator.isValid(validatingMail);

        if (isMailValid)
            return true;

        List<String> errorMessages = mailValidator.getMessageList();
        String messageTemplate = String.join(",", errorMessages);

        constraintValidatorContext.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();

        return false;
    }
}
