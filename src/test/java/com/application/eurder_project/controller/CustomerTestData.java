package com.application.eurder_project.controller;

import com.application.eurder_project.domain.Address;
import com.application.eurder_project.service.request.PostCustomerRequest;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTestData {

    private CustomerTestData() {
    }

    public static PostCustomerRequest validRequest() {
        return validRequest("Jane", "jane.doe@example.com");
    }

    public static PostCustomerRequest validRequest(String firstname, String email) {
        return new PostCustomerRequest(firstname,
                                       "Doe",
                                       email,
                                       "0471234567",
                                       "Main Street",
                                       12,
                                       "2B",
                                       "1000",
                                       "Belgium");
    }

}