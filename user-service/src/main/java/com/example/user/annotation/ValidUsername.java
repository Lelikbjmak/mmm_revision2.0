package com.example.user.annotation;

import com.example.user.validator.UsernameConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UsernameConstraintValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ValidUsername {

    String message() default "Username is not valid.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
