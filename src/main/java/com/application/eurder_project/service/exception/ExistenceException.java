package com.application.eurder_project.service.exception;

public class ExistenceException extends RuntimeException {
    public ExistenceException(String message) {
        super(message);
    }

    public static ExistenceException notInDatabase(String objectType) {
        return new ExistenceException("This item is not found in the database.");
    }
}