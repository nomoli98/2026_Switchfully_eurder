package com.application.eurder_project.service.request;

import com.application.eurder_project.domain.Address;

public record PostCustomerRequest(String firstname,
                                  String lastname,
                                  String email,
                                  String phoneNumber,
                                  Address address) {
}
