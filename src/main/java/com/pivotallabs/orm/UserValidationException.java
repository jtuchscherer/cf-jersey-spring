package com.pivotallabs.orm;

import java.util.Map;

public class UserValidationException extends RuntimeException {
    private User user;
    private Map<String, String> constraintViolations;

    public UserValidationException(User user, Map<String, String> constraintViolations) {
        this.user = user;
        this.constraintViolations = constraintViolations;
    }

    public Map<String, String> getConstraintViolations() {
        return constraintViolations;
    }

    public User getUser() {
        return user;
    }
}
