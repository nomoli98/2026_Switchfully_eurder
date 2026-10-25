package com.application.eurder_project.controller;

import com.application.eurder_project.domain.Item;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.EurderRepository;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.util.UUID;

import static com.application.eurder_project.controller.EurderShippingControllerTestData.eurderFor;
import static com.application.eurder_project.controller.EurderShippingControllerTestData.itemGroupShipping;
import static com.application.eurder_project.controller.EurderTestData.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EurderByShippingDateControllerTest {

    private static final String EURDERS = "/eurders";
    private static final String SHIPPING_TODAY = EURDERS + "/my-eurders/today";
    private static final String ALL_SHIPPING_TODAY = EURDERS + "/shipping-today";
    private static final String AUTHORIZATION = "Authorization";

    private User admin;
    private User customer;
    private UUID customerId;
    private Item cup;
    private Item spoon;
    private LocalDate today;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EurderRepository eurderRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.config = RestAssuredConfig.config()
                                              .jsonConfig(JsonConfig.jsonConfig()
                                                                    .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE));
        RestAssured.port = port;
        today = LocalDate.now();

        admin = UserTestData.validAdmin();
        userRepository.save(admin);

        customer = UserTestData.validCustomer();
        customerId = customer.getId();
        userRepository.save(customer);

        cup = cup();
        spoon = spoon();
        itemRepository.save(cup);
        itemRepository.save(spoon);
    }

    @AfterEach
    void tearDown() {
        RestAssured.reset();
        userRepository.reset();
        itemRepository.reset();
        eurderRepository.reset();
    }

    private ValidatableResponse getShippingTodayAs(UUID userId) {
        return given().header(AUTHORIZATION, userId)
                      .when()
                      .get(SHIPPING_TODAY)
                      .then();
    }

    private void postEurderAsCustomer(Item item, int amount) {
        given().header(AUTHORIZATION, customerId)
               .contentType(ContentType.JSON)
               .body(validRequest(item.getId(), amount))
               .when()
               .post(EURDERS)
               .then()
               .statusCode(201);
    }

    @Test
    @DisplayName("ShippingToday -> eurder with item shipping today is returned")
    void getShippingToday_whenItemShipsToday_returnsItemAndAddress() {
        eurderRepository.save(eurderFor(customer, itemGroupShipping(cup, 2, today)));

        getShippingTodayAs(customerId)
                .statusCode(200)
                .body("items", hasSize(1))
                .body("items[0].itemId", equalTo(cup.getId().toString()))
                .body("items[0].itemName", equalTo("Cup"))
                .body("items[0].amount", equalTo(2))
                .body("items[0].shippingDate", equalTo(today.toString()))
                .body("shippingAddress", notNullValue())
                .body("shippingAddress.street", equalTo(customer.getAddress().street()))
                .body("shippingAddress.city", equalTo(customer.getAddress().city()));
        ;

    }

    @Test
    @DisplayName("ShippingToday -> item groups of several eurders are combined")
    void getShippingToday_withSeveralEurdersShippingToday_returnsAllTheirItems() {
        eurderRepository.save(eurderFor(customer, itemGroupShipping(cup, 1, today)));
        eurderRepository.save(eurderFor(customer, itemGroupShipping(spoon, 2, today)));

        getShippingTodayAs(customerId)
                .statusCode(200)
                .body("items", hasSize(2))
                .body("items.itemName", containsInAnyOrder("Cup", "Spoon"));
    }

    @Test
    @DisplayName("ShippingToday -> only item groups shipping today are returned")
    void getShippingToday_withMixedShippingDates_returnsOnlyTodaysItems() {
        eurderRepository.save(eurderFor(customer,
                                        itemGroupShipping(cup, 1, today),
                                        itemGroupShipping(spoon, 2, today.plusDays(1))));

        getShippingTodayAs(customerId)
                .statusCode(200)
                .body("items", hasSize(1))
                .body("items[0].itemName", equalTo("Cup"));
    }

    @Test
    @DisplayName("ShippingToday -> eurders shipping tomorrow are not returned")
    void getShippingToday_whenNothingShipsToday_returnsEmptyItems() {
        postEurderAsCustomer(cup, 1); // enough stock, so it ships tomorrow

        getShippingTodayAs(customerId)
                .statusCode(200)
                .body("items", hasSize(0));
    }

    @Test
    @DisplayName("ShippingToday without any eurders -> empty items")
    void getShippingToday_whenNoEurdersExist_returnsEmptyItems() {
        getShippingTodayAs(customerId)
                .statusCode(200)
                .body("items", hasSize(0));
    }

    @Test
    @DisplayName("ShippingToday -> other users' eurders are not returned")
    void getShippingToday_doesNotIncludeOtherUsersEurders() {
        eurderRepository.save(eurderFor(admin, itemGroupShipping(spoon, 1, today)));
        eurderRepository.save(eurderFor(customer, itemGroupShipping(cup, 1, today)));

        getShippingTodayAs(customerId)
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
        getShippingTodayAs(UUID.randomUUID()).statusCode(401);
    }

    // ---------- admin: all items shipping today ----------

    private ValidatableResponse getAllShippingTodayAs(UUID userId) {
        return given().header(AUTHORIZATION, userId)
                      .when()
                      .get(ALL_SHIPPING_TODAY)
                      .then();
    }

    @Test
    @DisplayName("AllShippingToday as admin -> one entry per eurder with its items and address")
    void getAllShippingToday_asAdmin_returnsOneEntryPerEurderWithAddress() {
        eurderRepository.save(eurderFor(customer, itemGroupShipping(cup, 1, today)));
        eurderRepository.save(eurderFor(admin, itemGroupShipping(spoon, 2, today)));

        getAllShippingTodayAs(admin.getId())
                .statusCode(200)
                .body("", hasSize(2))
                .body("items.itemName", containsInAnyOrder(contains("Cup"), contains("Spoon")))
                .body("shippingAddress.street", everyItem(equalTo(customer.getAddress().street())))
                .body("shippingAddress.city", everyItem(equalTo(customer.getAddress().city())));
    }

    @Test
    @DisplayName(
            "AllShippingToday -> only item groups shipping today, eurders without any are left out")
    void getAllShippingToday_returnsOnlyTodaysItemGroups() {
        eurderRepository.save(eurderFor(customer,
                                        itemGroupShipping(cup, 1, today),
                                        itemGroupShipping(spoon, 2, today.plusDays(1))));
        eurderRepository.save(eurderFor(customer, itemGroupShipping(spoon, 1, today.plusDays(7))));

        getAllShippingTodayAs(admin.getId())
                .statusCode(200)
                .body("", hasSize(1))
                .body("[0].items", hasSize(1))
                .body("[0].items[0].itemName", equalTo("Cup"));
    }

    @Test
    @DisplayName("AllShippingToday when nothing ships today -> empty list")
    void getAllShippingToday_whenNothingShipsToday_returnsEmptyList() {
        postEurderAsCustomer(cup, 1); // ships tomorrow

        getAllShippingTodayAs(admin.getId())
                .statusCode(200)
                .body("", hasSize(0));
    }

    @Test
    @DisplayName("AllShippingToday as customer -> 403")
    void getAllShippingToday_asCustomer_returns403() {
        getAllShippingTodayAs(customerId).statusCode(403);
    }

    @Test
    @DisplayName("AllShippingToday without Authorization header -> 401")
    void getAllShippingToday_withoutAuthorizationHeader_returns401() {
        given().when().get(ALL_SHIPPING_TODAY).then().statusCode(401);
    }
}
