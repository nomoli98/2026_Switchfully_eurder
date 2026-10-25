package com.application.eurder_project.controller.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ItemGroupDto(UUID itemId,
                           String itemName,
                           int amount,
                           double groupPrice,
                           LocalDate shippingDate) {
}
