package com.application.eurder_project.controller;

import static org.junit.jupiter.api.Assertions.*;

class EurderByShippingDateControllerTest {
    private static final String SHIPPING_TODAY = "/eurders/my-orders/today"; // adjust to your controller's base path

    @Test
    @DisplayName("ShippingToday -> eurder with item shipping today is returned")
    void getShippingToday_whenItemShipsToday_returnsItemAndAddress() {
        eurderRepository.save(eurderFor(customer, itemGroupShipping(cup, 2, LocalDate.now())));

        given().header(AUTHORIZATION, customerId)
               .when()
               .get(SHIPPING_TODAY)
               .then()
               .statusCode(200)
               .body("items", hasSize(1))
               .body("items[0].itemId", equalTo(cup.getId().toString()))
               .body("items[0].itemName", equalTo("Cup"))
               .body("items[0].amount", equalTo(2))
               .body("items[0].shippingDate", equalTo(LocalDate.now().toString()))
               .body("address", notNullValue());
    }

    @Test
    @DisplayName("ShippingToday -> item groups of several eurders are combined")
    void getShippingToday_withSeveralEurdersShippingToday_returnsAllTheirItems() {
        eurderRepository.save(eurderFor(customer, itemGroupShipping(cup, 1, LocalDate.now())));
        eurderRepository.save(eurderFor(customer, itemGroupShipping(spoon, 2, LocalDate.now())));

        given().header(AUTHORIZATION, customerId)
               .when()
               .get(SHIPPING_TODAY)
               .then()
               .statusCode(200)
               .body("items", hasSize(2))
               .body("items.itemName", containsInAnyOrder("Cup", "Spoon"));
    }

    @Test
    @DisplayName("ShippingToday -> only item groups shipping today are returned")
    void getShippingToday_withMixedShippingDates_returnsOnlyTodaysItems() {
        eurderRepository.save(eurderFor(customer,
                                        itemGroupShipping(cup, 1, LocalDate.now()),
                                        itemGroupShipping(spoon, 2, LocalDate.now().plusDays(1))));

        given().header(AUTHORIZATION, customerId)
               .when()
               .get(SHIPPING_TODAY)
               .then()
               .statusCode(200)
               .body("items", hasSize(1))
               .body("items[0].itemName", equalTo("Cup"));
    }

    @Test
    @DisplayName("ShippingToday -> eurders shipping tomorrow are not returned")
    void getShippingToday_whenNothingShipsToday_returnsEmptyItems() {
        postEurder(customerId, validRequest(cup.getId(), 1)); // ships tomorrow

        given().header(AUTHORIZATION, customerId)
               .when()
               .get(SHIPPING_TODAY)
               .then()
               .statusCode(200)
               .body("items", hasSize(0));
    }

    @Test
    @DisplayName("ShippingToday without any eurders -> empty items")
    void getShippingToday_whenNoEurdersExist_returnsEmptyItems() {
        given().header(AUTHORIZATION, customerId)
               .when()
               .get(SHIPPING_TODAY)
               .then()
               .statusCode(200)
               .body("items", hasSize(0));
    }

    @Test
    @DisplayName("ShippingToday -> other users' eurders are not returned")
    void getShippingToday_doesNotIncludeOtherUsersEurders() {
        eurderRepository.save(eurderFor(admin, itemGroupShipping(spoon, 1, LocalDate.now())));
        eurderRepository.save(eurderFor(customer, itemGroupShipping(cup, 1, LocalDate.now())));

        given().header(AUTHORIZATION, customerId)
               .when()
               .get(SHIPPING_TODAY)
               .then()
               .statusCode(200)
               .body("items", hasSize(1))
               .body("items[0].itemName", equalTo("Cup"));
    }

    @Test
    @DisplayName("ShippingToday without Authorization header -> 401")
    void getShippingToday_withoutAuthorizationHeader_returns401() {
        given().when().get(SHIPPING_TODAY).then().statusCode(401);
    }

    @Test
    @DisplayName("ShippingToday with unknown user id -> 401")
    void getShippingToday_withUnknownUser_returns401() {
        given().header(AUTHORIZATION, UUID.randomUUID()).when().get(SHIPPING_TODAY).then().statusCode(401);
    }
}