package com.example.user.validator;

import com.example.user.service.UserDaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class UsernameValidator implements Validator<String> {

    @Autowired
    private UserDaoService userService;

    private final List<String> messages;

    private static final String USERNAME_PATTERN = "^[A-Za-z]\\w{5,24}$";

    private static final Pattern usernamePattern = Pattern.compile(USERNAME_PATTERN);

    @Autowired
    public UsernameValidator() {
        this.messages = new ArrayList<>();
    }

    @Override
    public boolean isValid(String username) {

        int usernameLength = username.length();

        if (username.isEmpty()) {
            messages.add("Username is mandatory.");
            return false;
        }

        if (usernameLength < 5) {
            messages.add("Username must contain at least 5 symbols.");
            return false;
        } else if (usernameLength > 25) {
            messages.add("Username must contain at least 5 and no more than 25 symbols.");
            return false;
        }

        if (!usernamePattern.matcher(username).matches()) {
            messages.add("Username must contain only letters: [a-Z], digits: [0-9] and underscore [_]");
            return false;
        }

        if (userService.existsByUsername(username)) {
            messages.add("Username already used.");
            return false;
        }

        return true;
    }

    public List<String> getMessages() {
        return messages;
    }

}
