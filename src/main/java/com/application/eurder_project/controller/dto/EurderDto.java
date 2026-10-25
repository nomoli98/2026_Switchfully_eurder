package com.application.eurder_project.controller.dto;


import java.util.List;
import java.util.UUID;

public record EurderDto(UUID id, double totalPrice, List<ItemGroupDto> items, UUID userId) {
}

