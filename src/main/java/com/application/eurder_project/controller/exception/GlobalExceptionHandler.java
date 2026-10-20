package com.application.eurder_project.controller.exception;

import com.application.eurder_project.security.UnauthorizedException;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.exception.ForbiddenException;
import com.application.eurder_project.service.exception.RequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleRequestException(RequestException exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(ExistenceException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleExistenceException(ExistenceException exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, String> handleUnauthorized(UnauthorizedException exception) {
        return Map.of("message", exception.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleForbidden(ForbiddenException exception) {
        return Map.of("message", exception.getMessage());
    }
}