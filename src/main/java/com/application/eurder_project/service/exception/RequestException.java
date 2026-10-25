package com.application.eurder_project.service.exception;

public class RequestException extends RuntimeException {

    public RequestException(String message) {
        super(message);
    }

    public static RequestException notFilledInField(String field) {
        return new RequestException("This field (" + field + ") must be filled in.");
    }

    public static RequestException invalidField(String field) {
        return new RequestException("This field (" + field + ") is invalid.");
    }

    public static RequestException negativeNumber(String field) {
        return new RequestException("The number on the field " + field + " cannot be negative.");
    }

    public static RequestException notPositiveNumber(String field) {
        return new RequestException("The number on the field " + field + " must be greater than 0.");
    }
}
