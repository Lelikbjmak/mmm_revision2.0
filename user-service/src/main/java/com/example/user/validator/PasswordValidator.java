package com.example.user.validator;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class PasswordValidator implements Validator<String> {

    private final List<String> messageList;

    private final Pattern passwordPatternMatcher;

    public PasswordValidator(@Value(value = "${validator.password.pattern}") String passwordPattern) {
        this.messageList = new ArrayList<>();
        this.passwordPatternMatcher = Pattern.compile(passwordPattern);
    }

    @Override
    public boolean isValid(String validatingPassword) {

        if (validatingPassword.isEmpty()) {
            messageList.add("Password is mandatory");
            return false;
        }

        if (validatingPassword.length() < 8) {
            messageList.add("Password must contain at least 8 symbols.");
            return false;
        } else if (validatingPassword.length() > 25) {
            messageList.add("Password must contain at least 8 and no more than 25 symbols.");
            return false;
        }

        if (passwordPatternMatcher.matcher(validatingPassword).matches()) {
            messageList.add("Password must contain at least 1 [A-Z], 1 [a-z], 1 [0-9] symbols.");
            return false;
        }

        return true;
    }

    public List<String> getMessageList() {
        return messageList;
    }
}
