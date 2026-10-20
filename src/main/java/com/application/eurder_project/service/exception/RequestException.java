package com.application.eurder_project.service.exception;

public class RequestException extends RuntimeException {

    public RequestException(String s) {
        super(s);
    }

    public static RequestException notFilledOutField(String field) {
        return new RequestException("This field (" + field + ") must be filled in.");
    }

    public static RequestException invalidField(String field) {
        return new RequestException("This field (" + field + ") is invalid.");
    }


}
