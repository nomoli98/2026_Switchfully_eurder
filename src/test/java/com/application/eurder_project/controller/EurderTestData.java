package com.application.eurder_project.controller;

import com.application.eurder_project.domain.Item;
import com.application.eurder_project.service.request.PostItemGroupRequest;
import com.application.eurder_project.service.request.PostEurderRequest;

import java.util.List;
import java.util.UUID;

class EurderTestData {

    private EurderTestData() {
    }

    public static Item cup() {
        return new Item("Cup", "Object to pour liquid", 10.99, 5);
    }

    public static Item spoon() {
        return new Item("Spoon", "Object to scoop liquid", 3.99, 10);
    }

    public static PostEurderRequest validRequest(UUID itemId, int amount) {
        return new PostEurderRequest(List.of(new PostItemGroupRequest(itemId, amount)));
    }
}