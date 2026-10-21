package com.application.eurder_project.controller;

import com.application.eurder_project.domain.Item;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.EurderRepository;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.repository.UserRepository;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EurderByUserControllerTest {

    private static final String EURDERS_BY_USER = "/eurders/user/";
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
    private EurderRepository eurderRepository;

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
        eurderRepository.reset();
    }

    @Test
    @DisplayName("GetByUser as customer -> 403")
    void getByUser_asCustomer_returns403() {
        given().header(AUTHORIZATION, customerId)
               .when()
               .get(EURDERS_BY_USER + adminId)
               .then()
               .statusCode(403);
    }

    @Test
    @DisplayName("GetByUser as admin -> 200")
    void getByUser_asAdmin_returns200() {
        given().header(AUTHORIZATION, adminId)
               .when()
               .get(EURDERS_BY_USER + adminId)
               .then()
               .statusCode(200);
    }

    @Test
    @DisplayName("GetByUser with invalid userId -> 404")
    void getByUser_withInvalidUserId_returns404() {
        given().header(AUTHORIZATION, adminId)
               .when()
               .get(EURDERS_BY_USER + UUID.randomUUID())
               .then()
               .statusCode(404);
    }

    @Test
    @DisplayName("GetByUser without Authorization header -> 401")
    void getByEurder_withoutAuthorizationHeader_returns401() {
        given().when().get(EURDERS_BY_USER + customerId).then().statusCode(401);
    }
}