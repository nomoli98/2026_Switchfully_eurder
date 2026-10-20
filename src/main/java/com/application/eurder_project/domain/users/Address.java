package com.application.eurder_project.domain.users;

public record Address(String street,
                      int number,
                      String apartment,
                      String postalCode,
                      String country) {
}
