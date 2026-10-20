package com.application.eurder_project.service.request;

import com.application.eurder_project.domain.ItemGroup;
import com.application.eurder_project.domain.user.User;

import java.util.List;

public record PostOrderRequest(double totalPrice, List<ItemGroup> items, User user) {
}
