package com.application.eurder_project.controller.dto;

import java.util.UUID;

public record ItemDto(UUID id, String name, String description, double price, int stock) {
}
