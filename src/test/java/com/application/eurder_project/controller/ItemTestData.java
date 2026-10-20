package com.application.eurder_project.controller;

import com.application.eurder_project.service.request.PostItemRequest;

class ItemTestData {

    public ItemTestData() {
    }
    public static PostItemRequest validRequest(){
        return new PostItemRequest("Cup", "Object to pour liquid", 10.99, 5);
    }
}