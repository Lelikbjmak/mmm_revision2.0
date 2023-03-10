package com.example.user.annotation;

import com.example.user.validator.EmailConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailConstraintValidator.class)
public @interface ValidEmail {


    String message() default "Invalid mail.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
