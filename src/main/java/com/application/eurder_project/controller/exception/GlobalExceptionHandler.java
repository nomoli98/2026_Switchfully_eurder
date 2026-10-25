package com.application.eurder_project.controller.exception;

import com.application.eurder_project.security.ForbiddenException;
import com.application.eurder_project.security.UnauthorizedException;
import com.application.eurder_project.service.exception.ConflictException;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.exception.RequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RequestException.class)
    public ResponseEntity<Map<String, String>> handleRequestException(RequestException exception) {
        logger.warn("Bad request: {}", exception.getMessage());
        return response(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ExistenceException.class)
    public ResponseEntity<Map<String, String>> handleExistenceException(ExistenceException exception) {
        logger.warn("Not found: {}", exception.getMessage());
        return response(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Map<String, String>> handleUnauthorized(UnauthorizedException exception) {
        logger.warn("Unauthorized access: {}", exception.getMessage());
        return response(HttpStatus.UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Map<String, String>> handleForbidden(ForbiddenException exception) {
        logger.warn("Forbidden access: {}", exception.getMessage());
        return response(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflict(ConflictException exception) {
        logger.warn("Conflict: {}", exception.getMessage());
        return response(HttpStatus.CONFLICT, exception.getMessage());
    }


    //    An Authorization header that is not a UUID is an invalid credential (401).
    //    Any other value that is not a UUID (e.g. in the path) is a bad request (400).
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception) {
        if (exception.getParameter().hasParameterAnnotation(RequestHeader.class)) {
            logger.warn("Unauthorized access: the Authorization header is not a valid id.");
            return response(HttpStatus.UNAUTHORIZED,
                            UnauthorizedException.invalidCredentials().getMessage());
        }
        String message = "The value of '" + exception.getName() + "' is invalid.";
        logger.warn("Bad request: {}", message);
        return response(HttpStatus.BAD_REQUEST, message);
    }

    private ResponseEntity<Map<String, String>> response(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("message", message));
    }
}
