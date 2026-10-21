package com.application.eurder_project.controller;

import com.application.eurder_project.domain.Item;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.repository.OrderRepository;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.service.request.PostItemGroupRequest;
import com.application.eurder_project.service.request.PostOrderRequest;
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

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.application.eurder_project.controller.OrderTestData.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrderControllerTest {

    private static final String ORDERS = "/orders";
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
    private OrderRepository orderRepository;

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

    private String postOrder(UUID authorizedId, PostOrderRequest request) {
        return given().header(AUTHORIZATION, authorizedId)
                      .contentType(ContentType.JSON)
                      .body(request)
                      .when()
                      .post(ORDERS)
                      .then()
                      .statusCode(201)
                      .extract()
                      .path("id");
    }

    private int currentStock(Item item) {
        return itemRepository.get(item.getId()).orElseThrow().getStock();
    }

    // ---------- GET /orders ----------

    @Test
    @DisplayName("GetAll -> EmptyList")
    void getAllOrders_whenNoOrdersExist_returnsEmptyList() {
        given().header(AUTHORIZATION, adminId)
               .when()
               .get(ORDERS)
               .then()
               .statusCode(200)
               .contentType(ContentType.JSON)
               .body("", hasSize(0));
    }

    @Test
    @DisplayName("CreateOrders -> GetAll -> List of Orders")
    void createdOrders_appearInTheOrdersList() {
        String first = postOrder(customerId, validRequest(cup.getId(), 1));
        String second = postOrder(customerId, validRequest(spoon.getId(), 2));

        given().header(AUTHORIZATION, adminId)
               .when()
               .get(ORDERS)
               .then()
               .statusCode(200)
               .body("", hasSize(2))
               .body("id", containsInAnyOrder(first, second));
    }

    // ---------- POST /orders: happy paths ----------

    @Test
    @DisplayName("CreateOrder -> 201 and Order")
    void createOrder_withValidRequest_returns201AndTheCreatedOrder() {
        given().header(AUTHORIZATION, customerId)
               .contentType(ContentType.JSON)
               .body(validRequest(cup.getId(), 2))
               .when()
               .post(ORDERS)
               .then()
               .statusCode(201)
               .body("id", notNullValue())
               .body("userId", equalTo(customerId.toString()))
               .body("totalPrice", closeTo(2 * 10.99, 0.001))
               .body("items", hasSize(1))
               .body("items[0].itemId", equalTo(cup.getId().toString()))
               .body("items[0].itemName", equalTo("Cup"))
               .body("items[0].amount", equalTo(2))
               .body("items[0].groupPrice", closeTo(2 * 10.99, 0.001));
    }

    @Test
    @DisplayName("CreateOrder with several item groups -> total is the sum of the groups")
    void createOrder_withMultipleItemGroups_returnsSummedTotalPrice() {
        PostOrderRequest request = new PostOrderRequest(List.of(
                new PostItemGroupRequest(cup.getId(), 2),
                new PostItemGroupRequest(spoon.getId(), 3)));

        given().header(AUTHORIZATION, customerId)
               .contentType(ContentType.JSON)
               .body(request)
               .when()
               .post(ORDERS)
               .then()
               .statusCode(201)
               .body("items", hasSize(2))
               .body("totalPrice", closeTo(2 * 10.99 + 3 * 3.99, 0.001));
    }

    @Test
    @DisplayName("CreateOrder as admin -> 201")
    void createOrder_asAdmin_returns201() {
        given().header(AUTHORIZATION, adminId)
               .contentType(ContentType.JSON)
               .body(validRequest(cup.getId(), 1))
               .when()
               .post(ORDERS)
               .then()
               .statusCode(201)
               .body("userId", equalTo(adminId.toString()));
    }

    // ---------- POST /orders: stock and shipping date ----------

    @Test
    @DisplayName("CreateOrder with enough stock -> ships tomorrow")
    void createOrder_whenStockCoversAmount_shipsTomorrow() {
        given().header(AUTHORIZATION, customerId)
               .contentType(ContentType.JSON)
               .body(validRequest(cup.getId(), 3))
               .when()
               .post(ORDERS)
               .then()
               .statusCode(201)
               .body("items[0].shippingDate", equalTo(LocalDate.now().plusDays(1).toString()));
    }

    @Test
    @DisplayName("CreateOrder for exactly the available stock -> ships tomorrow")
    void createOrder_whenAmountEqualsStock_shipsTomorrow() {
        given().header(AUTHORIZATION, customerId)
               .contentType(ContentType.JSON)
               .body(validRequest(cup.getId(), 5))
               .when()
               .post(ORDERS)
               .then()
               .statusCode(201)
               .body("items[0].shippingDate", equalTo(LocalDate.now().plusDays(1).toString()));
    }

    @Test
    @DisplayName("CreateOrder above available stock -> ships in 7 days")
    void createOrder_whenAmountExceedsStock_shipsInSevenDays() {
        given().header(AUTHORIZATION, customerId)
               .contentType(ContentType.JSON)
               .body(validRequest(cup.getId(), 6))
               .when()
               .post(ORDERS)
               .then()
               .statusCode(201)
               .body("items[0].shippingDate", equalTo(LocalDate.now().plusDays(7).toString()));
    }

    @Test
    @DisplayName("CreateOrder -> stock is reduced")
    void createOrder_reducesTheStockOfTheItem() {
        postOrder(customerId, validRequest(cup.getId(), 2));

        org.junit.jupiter.api.Assertions.assertEquals(3, currentStock(cup));
    }

    // ---------- POST /orders: validation ----------

    static Stream<Arguments> invalidRequests() {
        return Stream.of(
                arguments("amount equals zero",
                          (Function<UUID, PostOrderRequest>) itemId -> validRequest(itemId, 0)),
                arguments("amount is negative",
                          (Function<UUID, PostOrderRequest>) itemId -> validRequest(itemId, -3)),
                arguments("items list is empty",
                          (Function<UUID, PostOrderRequest>) itemId -> new PostOrderRequest(List.of())),
                arguments("items list is null",
                          (Function<UUID, PostOrderRequest>) itemId -> new PostOrderRequest(null)),
                arguments("item id is null",
                          (Function<UUID, PostOrderRequest>) itemId -> new PostOrderRequest(
                                  List.of(new PostItemGroupRequest(null, 1)))),
                arguments("one item group is null",
                          (Function<UUID, PostOrderRequest>) itemId -> new PostOrderRequest(
                                  Arrays.asList(new PostItemGroupRequest(itemId, 1), null)))
        );
    }

    @ParameterizedTest(name = "{0} -> 400")
    @MethodSource("invalidRequests")
    @DisplayName("Invalid Request fields -> 400")
    void createOrder_withInvalidRequest_returns400(String description,
                                                   Function<UUID, PostOrderRequest> requestForItem) {
        given().header(AUTHORIZATION, customerId)
               .contentType(ContentType.JSON)
               .body(requestForItem.apply(cup.getId()))
               .when()
               .post(ORDERS)
               .then()
               .statusCode(400);
    }

    @Test
    @DisplayName("CreateOrder with unknown item -> 404")
    void createOrder_withUnknownItem_returns404() {
        given().header(AUTHORIZATION, customerId)
               .contentType(ContentType.JSON)
               .body(validRequest(UUID.randomUUID(), 1))
               .when()
               .post(ORDERS)
               .then()
               .statusCode(404);
    }

    @Test
    @DisplayName("Invalid order -> stock is untouched")
    void createOrder_withInvalidRequest_doesNotChangeStock() {
        PostOrderRequest request = new PostOrderRequest(List.of(
                new PostItemGroupRequest(cup.getId(), 2),
                new PostItemGroupRequest(UUID.randomUUID(), 1)));

        given().header(AUTHORIZATION, customerId)
               .contentType(ContentType.JSON)
               .body(request)
               .when()
               .post(ORDERS)
               .then()
               .statusCode(404);

        org.junit.jupiter.api.Assertions.assertEquals(5, currentStock(cup));
    }

    // ---------- Security ----------

    @Test
    @DisplayName("CreateOrder without Authorization header -> 401")
    void createOrder_withoutAuthorizationHeader_returns401() {
        given().contentType(ContentType.JSON)
               .body(validRequest(cup.getId(), 1))
               .when()
               .post(ORDERS)
               .then()
               .statusCode(401);
    }

    @Test
    @DisplayName("CreateOrder with unknown user id -> 401")
    void createOrder_withUnknownUser_returns401() {
        given().header(AUTHORIZATION, UUID.randomUUID())
               .contentType(ContentType.JSON)
               .body(validRequest(cup.getId(), 1))
               .when()
               .post(ORDERS)
               .then()
               .statusCode(401);
    }

    @Test
    @DisplayName("GetAll without Authorization header -> 401")
    void getAllOrders_withoutAuthorizationHeader_returns401() {
        given().when().get(ORDERS).then().statusCode(401);
    }

    @Test
    @DisplayName("GetAll as customer -> 403")
    void getAllOrders_asCustomer_returns403() {
        given().header(AUTHORIZATION, customerId)
               .when()
               .get(ORDERS)
               .then()
               .statusCode(403);
    }
}