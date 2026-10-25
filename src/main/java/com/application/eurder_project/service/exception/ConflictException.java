package com.application.eurder_project.service.exception;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public static ConflictException emailAlreadyInUse() {
        return new ConflictException("This email is already in use.");
    }
}
