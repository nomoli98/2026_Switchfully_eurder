package com.application.eurder_project.service.request;

public record PutItemRequest(String name, String description, double price, int stock) {
}
