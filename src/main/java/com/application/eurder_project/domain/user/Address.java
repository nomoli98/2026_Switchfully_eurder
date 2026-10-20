package com.application.eurder_project.domain.user;

public record Address(String street,
                      int number,
                      String apartment,
                      String postalCode,
                      String country) {
}
