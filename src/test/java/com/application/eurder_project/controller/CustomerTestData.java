package com.application.eurder_project.controller;

import com.application.eurder_project.domain.Role;
import com.application.eurder_project.service.request.PostUserRequest;

class CustomerTestData {

    private CustomerTestData() {
    }

    public static PostUserRequest validRequest() {
        return validRequest("Jane", "jane.doe@example.com");
    }

    public static PostUserRequest validRequest(String firstname, String email) {
        return new PostUserRequest(firstname,
                                   "Doe",
                                   email,
                                   "0471234567",
                                   "Main Street",
                                   12,
                                   "2B",
                                   "1000",
                                   "Belgium",
                                   Role.CUSTOMER);
    }

}