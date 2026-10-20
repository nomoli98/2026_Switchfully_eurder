package com.application.eurder_project.controller;

import com.application.eurder_project.domain.Address;
import com.application.eurder_project.domain.Role;
import com.application.eurder_project.domain.User;
import com.application.eurder_project.service.request.PostUserRequest;

class UserTestData {

    private UserTestData() {
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

    public static User validAdmin() {
        return new User("Admin",
                        "Doe",
                        "admin@example.com",
                        "0471234567",
                        new Address(
                                "Main Street",
                                12,
                                "2B",
                                "1000",
                                "Belgium"),
                        Role.ADMIN);
    }

}