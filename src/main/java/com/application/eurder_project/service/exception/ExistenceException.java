package com.application.eurder_project.service.exception;

public class ExistenceException extends RuntimeException {
    public ExistenceException(String message) {
        super(message);
    }

    public static ExistenceException NotInDatabase(String objectType){
        return new ExistenceException("This " + objectType + " is not found in the database.");
    }
}
