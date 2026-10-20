package com.application.eurder_project.controller;

import com.application.eurder_project.controller.dto.ItemDto;
import com.application.eurder_project.domain.Item;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.service.request.PostItemRequest;
import com.application.eurder_project.service.request.PostUserRequest;
import io.restassured.RestAssured;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.path.json.config.JsonPathConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.lang.classfile.attribute.PermittedSubclassesAttribute;
import java.util.UUID;
import java.util.stream.Stream;

import static com.application.eurder_project.controller.ItemTestData.validRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemControllerTest {

    private static final String ITEMS = "/items";
    private static final String AUTHORIZATION = "Authorization";
    private User admin;
    private UUID adminId;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

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
    }

    @AfterEach
    void tearDown() {
        RestAssured.reset();
        userRepository.reset();
        itemRepository.reset();
    }

    private String postItem(PostItemRequest request) {
        return given().header(AUTHORIZATION, adminId)
                      .contentType(ContentType.JSON)
                      .body(request)
                      .when()
                      .post(ITEMS)
                      .then()
                      .statusCode(201)
                      .extract()
                      .path("id");
    }

    @Test
    @DisplayName("GetAll -> EmptyList")
    void getAllItems_whenNoCustomersExist_returnsEmptyList() {
        given().when()
               .get(ITEMS)
               .then()
               .statusCode(200)
               .contentType(ContentType.JSON)
               .body("", hasSize(0));
    }

    @Test
    @DisplayName("CreateCustomer -> 201 and Customer")
    void createCustomer_withValidRequest_returns201AndTheCreatedCustomer() {
        given().header(AUTHORIZATION, adminId)
               .contentType(ContentType.JSON)
               .body(validRequest())
               .when()
               .post(ITEMS)
               .then()
               .statusCode(201)
               .body("id", notNullValue())
               .body("name", equalTo("Cup"))
               .body("description", equalTo("Object to pour liquid"))
               .body("price", equalTo(10.99))
               .body("stock", equalTo(5));
    }

    @Test
    @DisplayName("CreateItems -> GetAll -> List of Items")
    void createdItems_appearInTheItemsList() {
        String cup = postItem(new PostItemRequest("Cup", "Object to pour liquid", 10.99, 5));
        String spoon = postItem(new PostItemRequest("Spoon", "Object to scoop liquid", 3.99, 10));

        given().when()
               .get(ITEMS)
               .then()
               .statusCode(200)
               .body("", hasSize(2))
               .body("id", containsInAnyOrder(cup, spoon))
               .body("name", containsInAnyOrder("Cup", "Spoon"));
    }


    static Stream<Arguments> invalidRequests() {
        return Stream.of(
                arguments("name is null",
                          new PostItemRequest(null, "Object to pour liquid", 10.99, 5)),
                arguments("description is null",
                          new PostItemRequest("Cup", null, 10.99, 5)),
                arguments("price equals zero",
                          new PostItemRequest("Cup", "Object to pour liquid", 0, 5)),
                arguments("price is negative",
                          new PostItemRequest("Cup", "Object to pour liquid", -10.99, 5)),
                arguments("stock equals zero",
                          new PostItemRequest("Cup", "Object to pour liquid", 10.99, 0)),
                arguments("stock is negative",
                          new PostItemRequest("Cup", "Object to pour liquid", 10.99, -5))
                        );
    }

    @ParameterizedTest(name = "{0} -> 400")
    @MethodSource("invalidRequests")
    @DisplayName("Invalid Request fields -> 400")
    void createCustomer_withInvalidRequest_returns400(String description, PostItemRequest request) {
        given().header(AUTHORIZATION, adminId)
               .contentType(ContentType.JSON)
               .body(request)
               .when()
               .post(ITEMS)
               .then()
               .statusCode(400);
    }

    @Test
    @DisplayName("GetOneItem needs no authorization -> Ok")
    void getOneById_withValidAuthorization_returns200() {

        ItemDto item = given().header(AUTHORIZATION, adminId)
                             .contentType(ContentType.JSON)
                             .body(validRequest())
                             .when()
                             .post(ITEMS)
                             .then()
                             .statusCode(201)
                             .extract()
                             .as(ItemDto.class);

        given()
                .contentType(ContentType.JSON)
                .when()
                .get(ITEMS + "/" + item.id())
                .then()
                .statusCode(200)
                .body("id", equalTo(item.id().toString()))
                .body("name", equalTo("Cup"));
    }

}