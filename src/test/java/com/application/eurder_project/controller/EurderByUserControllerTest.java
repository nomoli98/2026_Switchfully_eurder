package com.application.eurder_project.controller;

import com.application.eurder_project.domain.Item;
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

import java.util.UUID;

import static com.application.eurder_project.controller.EurderTestData.cup;
import static com.application.eurder_project.controller.EurderTestData.spoon;
import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EurderByUserControllerTest {
    private static final String EURDERS = "/orders";
    private static final String AUTHORIZATION = "Authorization";

    private User admin;
    private UUID adminId;
    private User customer;
    private UUID customerId;
    private Item cup;
    private Item spoon;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private EurderRepository orderRepository;

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
    }

    @AfterEach
    void tearDown() {
        RestAssured.reset();
        userRepository.reset();
        itemRepository.reset();
        orderRepository.reset();
    }

    private String postOrder(UUID authorizedId, PostEurderRequest request) {
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
    @DisplayName("GetByUser without Authorization header -> 401")
    void getEurdersByUser_withoutAuthorizationHeader_returns401() {
        given().when().get(EURDERS+ "/user/" + adminId).then().statusCode(401);
    }

    @Test
    @DisplayName("GetByUser as customer -> 403")
    void getEurdersByUser_asCustomer_returns403() {
        given().header(AUTHORIZATION, customerId)
               .when()
               .get(EURDERS+ "/user/" + adminId)
               .then()
               .statusCode(403);
    }

    @Test
    @DisplayName("GetByUser as admin with bad uuid -> 404")
    void getEurdersByUser_asAdmin_withBadPathVariable_returns404() {
        given().header(AUTHORIZATION, adminId)
               .when()
               .get(EURDERS+ "/user/" + UUID.randomUUID())
               .then()
               .statusCode(404);
    }


}