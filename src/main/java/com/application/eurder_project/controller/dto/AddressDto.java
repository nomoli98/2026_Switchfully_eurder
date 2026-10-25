package com.application.eurder_project.controller.dto;

public record AddressDto(String street,
                         int number,
                         String city,
                         String postalCode,
                         String country) {
}
