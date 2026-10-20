package com.application.eurder_project.controller.dto;

import com.application.eurder_project.domain.users.Address;

import java.util.UUID;

public record UserDto(UUID id,
                      String firstname,
                      String lastname,
                      String email,
                      String phoneNumber,
                      Address address) {
}
