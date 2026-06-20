package com.notifplatform.userservice.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String externalId) {
        super("user not found: " + externalId);
    }
}
