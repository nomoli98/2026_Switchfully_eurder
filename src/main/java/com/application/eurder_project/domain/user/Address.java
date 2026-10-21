package com.application.eurder_project.domain.user;

public record Address(String street,
                      Integer number,
                      String city,
                      String postalCode,
                      String country) {
}
