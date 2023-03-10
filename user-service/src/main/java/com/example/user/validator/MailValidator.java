package com.example.user.validator;

import com.example.user.service.UserDaoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class MailValidator implements Validator<String> {

    private final UserDaoService userDaoService;

    private final List<String> messageList;

    private final Pattern mailPatternMatcher;

    public MailValidator(UserDaoService userDaoService, @Value(value = "${validator.mail.pattern}") String mailPattern) {
        this.userDaoService = userDaoService;
        this.messageList = new ArrayList<>();
        this.mailPatternMatcher = Pattern.compile(mailPattern);
    }

    @Override
    public boolean isValid(String validatingMail) {

        if (validatingMail.isEmpty()) {
            messageList.add("Mail is mandatory.");
            return false;
        }

        if (!mailPatternMatcher.matcher(validatingMail).matches()) {
            messageList.add("Mail is not valid.");
            return false;
        }

        if (userDaoService.existsByEmail(validatingMail)) {
            messageList.add("Mail is already used.");
            return false;
        }

        return true;
    }

    public List<String> getMessageList() {
        return messageList;
    }
}

