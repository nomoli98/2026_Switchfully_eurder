package com.application.eurder_project.service.request;

import com.application.eurder_project.domain.user.Role;

public record PostUserRequest(String firstname,
                              String lastname,
                              String email,
                              String phoneNumber,
                              String street,
                              Integer number,
                              String apartment,
                              String postalCode,
                              String country,
                              Role role) {
}
