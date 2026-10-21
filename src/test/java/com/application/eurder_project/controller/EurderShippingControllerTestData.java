package com.application.eurder_project.controller;

import com.application.eurder_project.domain.Eurder;
import com.application.eurder_project.domain.Item;
import com.application.eurder_project.domain.ItemGroup;
import com.application.eurder_project.domain.user.User;

import java.time.LocalDate;
import java.util.List;

class EurderShippingControllerTestData {
    public static ItemGroup itemGroupShipping(Item item, int amount, LocalDate shippingDate) {
        return new ItemGroup(item, amount, shippingDate); // adjust to your ItemGroup constructor
    }

    public static Eurder eurderFor(User user, ItemGroup... groups) {
        return new Eurder(List.of(groups), user);
    }
}