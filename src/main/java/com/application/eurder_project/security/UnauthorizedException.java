package com.application.eurder_project.security;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }

    public static UnauthorizedException invalidCredentials() {
        return new UnauthorizedException("These credentials are invalid.");
    }
}
