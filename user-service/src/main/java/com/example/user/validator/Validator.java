package com.example.user.validator;

public interface Validator<T> {

    boolean isValid(T validatingField);
}
