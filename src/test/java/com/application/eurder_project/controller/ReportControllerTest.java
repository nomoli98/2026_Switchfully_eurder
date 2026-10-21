package com.application.eurder_project.controller;
import com.application.eurder_project.domain.Item;
import com.application.eurder_project.domain.Report;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.EurderRepository;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.service.request.PostEurderRequest;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;


import static com.application.eurder_project.controller.EurderTestData.*;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportControllerTest {
    private static final String EURDERS = "/eurders";
    private static final String REPORT = "/report";
    private static final String AUTHORIZATION = "Authorization";

    private User admin;
    private UUID adminId;
    private User customer;
    private UUID customerId;
    private Item cup;
    private Item spoon;

    @Autowired
    private EurderRepository eurderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.config = RestAssuredConfig.config()
                                              .jsonConfig(JsonConfig.jsonConfig()
                                                                    .numberReturnType(JsonPathConfig.NumberReturnType.DOUBLE));
        RestAssured.port = port;

        admin = UserTestData.validAdmin();
        adminId = admin.getId();
        userRepository.save(admin);

        customer = UserTestData.validCustomer();
        customerId = customer.getId();
        userRepository.save(customer);

        cup = cup();
        spoon = spoon();
        itemRepository.save(cup);
        itemRepository.save(spoon);

        String customerEurder = postEurder(customerId, validRequest(cup.getId(), 2));
        String adminEurder = postEurder(adminId, validRequest(cup.getId(), 2));
    }

    @AfterEach
    void tearDown() {
        RestAssured.reset();
        userRepository.reset();
        eurderRepository.reset();
    }

    private String postEurder(UUID authorizedId, PostEurderRequest request) {
        return given().header(AUTHORIZATION, authorizedId)
                      .contentType(ContentType.JSON)
                      .body(request)
                      .when()
                      .post(EURDERS)
                      .then()
                      .statusCode(201)
                      .extract()
                      .path("id");
    }

    @Test
    @DisplayName("createReport without Authorization header -> 401")
    void createReport_withoutAuthorizationHeader_returns401() {
        given().when().get(REPORT).then().statusCode(401);
    }

    @Test
    @DisplayName("createReport as a customer or admin -> 200")
    void createReport_asCustomerOrAdmin_returns403() {
        given().header(AUTHORIZATION, customerId).when().get(REPORT).then().statusCode(200);
        given().header(AUTHORIZATION, adminId).when().get(REPORT).then().statusCode(200);

    }

    @Test
    @DisplayName("createReport -> returns YOUR report")
    void createReport_asSpecificCustomer_returnsSpecificCustomersReport(){
        given().header(AUTHORIZATION, customerId)
               .contentType(ContentType.JSON)
               .when()
               .get(REPORT)
               .then()
               .statusCode(200)
               .body("eurderDtos[0].userId", equalTo(customerId.toString()));
    }







}