package com.application.eurder_project.service.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }

    public static Exception invalidPermission() {
        return null;
    }
}
