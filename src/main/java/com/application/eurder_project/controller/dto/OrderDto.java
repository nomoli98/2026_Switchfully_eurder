package com.application.eurder_project.controller.dto;

import com.application.eurder_project.domain.ItemGroup;
import com.application.eurder_project.domain.user.User;

import java.util.List;
import java.util.UUID;

public record OrderDto<items>(UUID id, double totalPrice, List<ItemGroup> items, User user) {
}
