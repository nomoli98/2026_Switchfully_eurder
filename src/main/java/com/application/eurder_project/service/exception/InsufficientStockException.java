package com.application.eurder_project.service.exception;

import java.util.UUID;

public class InsufficientStockException extends RuntimeException {
    public InsufficientStockException(UUID id) {
        super("The item " + id + " has an insufficient stock for this order");
    }
}
