package com.application.eurder_project.controller;

import com.application.eurder_project.service.request.PostItemRequest;
import com.application.eurder_project.service.request.PutItemRequest;

class ItemTestData {

    public ItemTestData() {
    }

    public static PostItemRequest validPostRequest() {
        return new PostItemRequest("Cup", "Object to pour liquid", 10.99, 5);
    }

    public static PutItemRequest validPutRequest() {
        return new PutItemRequest("Glass", null, null, null);
    }

    public static PutItemRequest invalidPutRequest() {
        return new PutItemRequest("Glass", null, -3.5, null);
    }
}