package com.application.eurder_project.service.response;

import com.application.eurder_project.domain.ItemGroup;
import com.application.eurder_project.domain.user.Address;

import java.util.List;

public record ShippedToday(List<ItemGroup> items, Address address) {
}