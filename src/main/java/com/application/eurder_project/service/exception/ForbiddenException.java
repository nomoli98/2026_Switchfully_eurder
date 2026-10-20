package com.application.eurder_project.service.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public static ForbiddenException invalidPermission() {
        return new ForbiddenException("You do not have the authorizations to go further.");
    }
}
