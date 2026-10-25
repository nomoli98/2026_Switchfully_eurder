package com.application.eurder_project.controller;

import com.application.eurder_project.controller.dto.ItemDto;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.service.request.PostItemRequest;
import com.application.eurder_project.service.request.PutItemRequest;
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

import java.util.UUID;
import java.util.stream.Stream;

import static com.application.eurder_project.controller.ItemTestData.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ItemControllerTest {

    private static final String ITEMS = "/items";
    private static final String AUTHORIZATION = "Authorization";
    private UUID adminId;
    private UUID customerId;

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
        User admin = UserTestData.validAdmin();
        adminId = admin.getId();
        userRepository.save(admin);
        User customer = UserTestData.validCustomer();
        customerId = customer.getId();
        userRepository.save(customer);
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
    void getAllItems_whenNoItemsExist_returnsEmptyList() {
        given().when()
               .get(ITEMS)
               .then()
               .statusCode(200)
               .contentType(ContentType.JSON)
               .body("", hasSize(0));
    }

    @Test
    @DisplayName("CreatItem -> 201 and Item")
    void createItems_withValidRequest_returns201AndTheCreatedItem() {
        given().header(AUTHORIZATION, adminId)
               .contentType(ContentType.JSON)
               .body(validPostRequest())
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
                          new PostItemRequest("Cup", "Object to pour liquid", 0.0, 5)),
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
                             .body(validPostRequest())
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

    @Test
    @DisplayName("Update Item without Authorization -> FORBIDDEN")
    void updateItem_withInvalidAuthorization_Return403(){
        ItemDto item = given().header(AUTHORIZATION, adminId)
                              .contentType(ContentType.JSON)
                              .body(validPostRequest())
                              .when()
                              .post(ITEMS)
                              .then()
                              .statusCode(201)
                              .extract()
                              .as(ItemDto.class);
        given().header(AUTHORIZATION, customerId)
                .contentType(ContentType.JSON)
                .body(validPutRequest())
                .when()
                .put(ITEMS + "/" + item.id())
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Update Item with Authorization -> OK")
    void updateItem_withValidAuthorization_Return200(){
        ItemDto item = given().header(AUTHORIZATION, adminId)
                              .contentType(ContentType.JSON)
                              .body(validPostRequest())
                              .when()
                              .post(ITEMS)
                              .then()
                              .statusCode(201)
                              .extract()
                              .as(ItemDto.class);
        given().header(AUTHORIZATION, adminId)
                .contentType(ContentType.JSON)
                .body(validPutRequest())
                .when()
                .put(ITEMS + "/" + item.id())
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("Update Item with Invalid Request -> BAD_REQUEST")
    void updateItem_withInvalidRequest_Return200(){
        ItemDto item = given().header(AUTHORIZATION, adminId)
                              .contentType(ContentType.JSON)
                              .body(validPostRequest())
                              .when()
                              .post(ITEMS)
                              .then()
                              .statusCode(201)
                              .extract()
                              .as(ItemDto.class);
        given().header(AUTHORIZATION, adminId)
                .contentType(ContentType.JSON)
                .body(invalidPutRequest())
                .when()
                .put(ITEMS + "/" + item.id())
                .then()
                .statusCode(400);
    }

    private void putItem(UUID authorization, String itemId, PutItemRequest request, int expectedStatus) {
        given().header(AUTHORIZATION, authorization)
               .contentType(ContentType.JSON)
               .body(request)
               .when()
               .put(ITEMS + "/" + itemId)
               .then()
               .statusCode(expectedStatus);
    }

    @Test
    @DisplayName("Update Item with only a price -> same id, only the price changes")
    void updateItem_withOnlyAPrice_changesOnlyThePrice() {
        String itemId = postItem(validPostRequest());

        given().header(AUTHORIZATION, adminId)
               .contentType(ContentType.JSON)
               .body(new PutItemRequest(null, null, 8.99, null))
               .when()
               .put(ITEMS + "/" + itemId)
               .then()
               .statusCode(200)
               .body("id", equalTo(itemId))
               .body("name", equalTo("Cup"))
               .body("description", equalTo("Object to pour liquid"))
               .body("price", equalTo(8.99))
               .body("stock", equalTo(5));

        given().when()
               .get(ITEMS + "/" + itemId)
               .then()
               .statusCode(200)
               .body("id", equalTo(itemId))
               .body("price", equalTo(8.99));
        given().when().get(ITEMS).then().body("", hasSize(1));
    }

    @Test
    @DisplayName("Update Item with a stock of 0 -> 200")
    void updateItem_withStockZero_returns200() {
        String itemId = postItem(validPostRequest());

        given().header(AUTHORIZATION, adminId)
               .contentType(ContentType.JSON)
               .body(new PutItemRequest(null, null, null, 0))
               .when()
               .put(ITEMS + "/" + itemId)
               .then()
               .statusCode(200)
               .body("stock", equalTo(0));
    }

    @Test
    @DisplayName("Update Item with a price of 0 or a negative stock -> 400")
    void updateItem_withZeroPriceOrNegativeStock_returns400() {
        String itemId = postItem(validPostRequest());

        putItem(adminId, itemId, new PutItemRequest(null, null, 0.0, null), 400);
        putItem(adminId, itemId, new PutItemRequest(null, null, null, -1), 400);
    }

    @Test
    @DisplayName("Update unknown Item -> 404")
    void updateItem_withUnknownId_returns404() {
        putItem(adminId, UUID.randomUUID().toString(), validPutRequest(), 404);
    }

    @Test
    @DisplayName("Update Item without Authorization header -> 401")
    void updateItem_withoutAuthorizationHeader_returns401() {
        String itemId = postItem(validPostRequest());

        given().contentType(ContentType.JSON)
               .body(validPutRequest())
               .when()
               .put(ITEMS + "/" + itemId)
               .then()
               .statusCode(401);
    }
}
