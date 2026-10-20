package com.application.eurder_project.domain.user;

public record Address(String street,
                      Integer number,
                      String apartment,
                      String postalCode,
                      String country) {
}
