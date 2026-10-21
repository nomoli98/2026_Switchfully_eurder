package com.application.eurder_project.controller.dto;

import java.util.List;

public record ShippedTodayDto(List<ItemGroupDto> items, AddressDto shippingAddress) {
}
