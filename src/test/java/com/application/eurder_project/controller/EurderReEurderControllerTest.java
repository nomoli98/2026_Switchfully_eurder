package com.application.eurder_project.controller;

import com.application.eurder_project.domain.Item;
import com.application.eurder_project.domain.user.Address;
import com.application.eurder_project.domain.user.Role;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.EurderRepository;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.service.request.PostEurderRequest;
import com.application.eurder_project.service.request.PostItemGroupRequest;
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
import java.util.List;
import java.util.UUID;

import static com.application.eurder_project.controller.EurderTestData.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EurderReEurderControllerTest {

    private static final String EURDERS = "/eurders";
    private static final String MY_EURDERS = "/eurders/my-eurders";
    private static final String REEURDER = "/eurders/my-eurders/re-eurder/{id}";
    private static final String AUTHORIZATION = "Authorization";

    private User admin;
    private UUID adminId;
    private User customer;
    private UUID customerId;
    private User otherCustomer;
    private UUID otherCustomerId;
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

        otherCustomer = new User("Other",
                                 "Doe",
                                 "other@example.com",
                                 "0471234567",
                                 new Address("Main Street", 12, "London", "1000", "Belgium"),
                                 Role.CUSTOMER);
        otherCustomerId = otherCustomer.getId();
        userRepository.save(otherCustomer);

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

    // ---------- helpers ----------

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

    private ValidatableResponse reeurder(UUID authorizedId, String eurderId) {
        return given().header(AUTHORIZATION, authorizedId)
                      .when()
                      .post(REEURDER, eurderId)
                      .then();
    }

    private String reeurderOk(UUID authorizedId, String eurderId) {
        return reeurder(authorizedId, eurderId).statusCode(201).extract().path("id");
    }

    private int currentStock(Item item) {
        return itemRepository.get(item.getId()).orElseThrow().getStock();
    }

    // ---------- happy path ----------

    @Test
    @DisplayName("Reeurder -> 201 and a copy of the previous Eurder")
    void reeurder_ofExistingEurder_returns201AndCopyOfTheItems() {
        String originalId = postEurder(customerId, validRequest(cup.getId(), 2));

        reeurder(customerId, originalId)
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
    @DisplayName("Reeurder -> new id, different from the original")
    void reeurder_returnsANewEurderId() {
        String originalId = postEurder(customerId, validRequest(cup.getId(), 1));

        String newId = reeurderOk(customerId, originalId);

        org.junit.jupiter.api.Assertions.assertNotEquals(originalId, newId);
    }

    @Test
    @DisplayName("Reeurder with several item groups -> every group is copied")
    void reeurder_withMultipleItemGroups_copiesAllGroupsAndTotal() {
        PostEurderRequest request
                = new PostEurderRequest(List.of(new PostItemGroupRequest(cup.getId(), 2),
                                                new PostItemGroupRequest(spoon.getId(), 3)));
        String originalId = postEurder(customerId, request);

        reeurder(customerId, originalId)
                .statusCode(201)
                .body("items", hasSize(2))
                .body("items.itemId",
                      containsInAnyOrder(cup.getId().toString(), spoon.getId().toString()))
                .body("totalPrice", closeTo(2 * 10.99 + 3 * 3.99, 0.001));
    }

    @Test
    @DisplayName("Reeurder as admin of an own Eurder -> 201")
    void reeurder_asAdminOfOwnEurder_returns201() {
        String originalId = postEurder(adminId, validRequest(cup.getId(), 1));

        reeurder(adminId, originalId)
                .statusCode(201)
                .body("userId", equalTo(adminId.toString()));
    }

    @Test
    @DisplayName("Reeurder of a Reeurder -> 201")
    void reeurder_ofAReeurder_returns201() {
        String originalId = postEurder(customerId, validRequest(spoon.getId(), 1));
        String firstReorder = reeurderOk(customerId, originalId);

        reeurder(customerId, firstReorder)
                .statusCode(201)
                .body("items[0].itemId", equalTo(spoon.getId().toString()))
                .body("items[0].amount", equalTo(1));
    }

    @Test
    @DisplayName("Reeurder the same Eurder twice -> two extra Eurders")
    void reeurder_sameEurderTwice_createsTwoSeparateEurders() {
        String originalId = postEurder(customerId, validRequest(spoon.getId(), 1));

        String first = reeurderOk(customerId, originalId);
        String second = reeurderOk(customerId, originalId);

        given().header(AUTHORIZATION, adminId)
               .when()
               .get(EURDERS)
               .then()
               .statusCode(200)
               .body("", hasSize(3))
               .body("id", containsInAnyOrder(originalId, first, second));
    }

    // ---------- persistence side effects ----------

    @Test
    @DisplayName("Reeurder -> original and new Eurder are both listed")
    void reeurder_keepsTheOriginalAndAddsTheNewEurderToTheList() {
        String originalId = postEurder(customerId, validRequest(cup.getId(), 1));
        String newId = reeurderOk(customerId, originalId);

        given().header(AUTHORIZATION, adminId)
               .when()
               .get(EURDERS)
               .then()
               .statusCode(200)
               .body("", hasSize(2))
               .body("id", containsInAnyOrder(originalId, newId));
    }

    @Test
    @DisplayName("Reeurder -> appears in my-eurders of the customer")
    void reeurder_appearsInMyEurders() {
        String originalId = postEurder(customerId, validRequest(cup.getId(), 1));
        String newId = reeurderOk(customerId, originalId);

        given().header(AUTHORIZATION, customerId)
               .when()
               .get(MY_EURDERS)
               .then()
               .statusCode(200)
               .body("", hasSize(2))
               .body("id", containsInAnyOrder(originalId, newId));
    }

    @Test
    @DisplayName("Reeurder -> other customers do not see the new Eurder")
    void reeurder_doesNotAppearInOtherCustomersEurders() {
        String originalId = postEurder(customerId, validRequest(cup.getId(), 1));
        reeurderOk(customerId, originalId);

        given().header(AUTHORIZATION, otherCustomerId)
               .when()
               .get(MY_EURDERS)
               .then()
               .statusCode(200)
               .body("", hasSize(0));
    }

    @Test
    @DisplayName("Reeurder -> stock is reduced a second time")
    void reeurder_reducesTheStockAgain() {
        String originalId = postEurder(customerId, validRequest(cup.getId(), 2));
        assertEquals(3, currentStock(cup));

        reeurderOk(customerId, originalId);

        assertEquals(1, currentStock(cup));
    }

    @Test
    @DisplayName("Reeurder with several item groups -> stock of every item is reduced")
    void reeurder_withMultipleItemGroups_reducesStockOfEveryItem() {
        PostEurderRequest request
                = new PostEurderRequest(List.of(new PostItemGroupRequest(cup.getId(), 2),
                                                new PostItemGroupRequest(spoon.getId(), 3)));
        String originalId = postEurder(customerId, request);

        reeurderOk(customerId, originalId);

        assertEquals(1, currentStock(cup));
        assertEquals(4, currentStock(spoon));
    }

    // ---------- shipping date ----------

    @Test
    @DisplayName("Reeurder with enough stock -> ships tomorrow")
    void reeurder_whenStockCoversAmount_shipsTomorrow() {
        String originalId = postEurder(customerId, validRequest(cup.getId(), 2));

        reeurder(customerId, originalId)
                .statusCode(201)
                .body("items[0].shippingDate", equalTo(LocalDate.now().plusDays(1).toString()));
    }

    @Test
    @DisplayName("Reeurder for exactly the remaining stock -> ships tomorrow")
    void reeurder_whenAmountEqualsRemainingStock_shipsTomorrow() {
        // stock 5 -> first order 2 -> remaining 3 -> reorder of 2 leaves 1, then a 3rd order of 1 fits
        String originalId = postEurder(customerId, validRequest(cup.getId(), 1));
        postEurder(customerId, validRequest(cup.getId(), 1));
        postEurder(customerId, validRequest(cup.getId(), 1));
        // remaining stock is 2, reorder amount is 1 -> fits; use a bigger one for the exact case
        String exactId = postEurder(otherCustomerId, validRequest(cup.getId(), 1));
        // remaining stock is now 1, reorder of an amount-1 order equals the stock exactly
        reeurder(otherCustomerId, exactId)
                .statusCode(201)
                .body("items[0].shippingDate", equalTo(LocalDate.now().plusDays(1).toString()));
        org.junit.jupiter.api.Assertions.assertNotNull(originalId);
    }

    @Test
    @DisplayName("Reeurder when the stock is used up -> ships in 7 days")
    void reeurder_whenStockIsExhausted_shipsInSevenDays() {
        String originalId = postEurder(customerId, validRequest(cup.getId(), 5));

        reeurder(customerId, originalId)
                .statusCode(201)
                .body("items[0].shippingDate", equalTo(LocalDate.now().plusDays(7).toString()));
    }

    @Test
    @DisplayName("Reeurder above the remaining stock -> ships in 7 days")
    void reeurder_whenAmountExceedsRemainingStock_shipsInSevenDays() {
        String originalId = postEurder(customerId, validRequest(cup.getId(), 3));
        // remaining stock is 2, reorder wants 3
        reeurder(customerId, originalId)
                .statusCode(201)
                .body("items[0].shippingDate", equalTo(LocalDate.now().plusDays(7).toString()));
    }

    @Test
    @DisplayName("Reeurder with mixed stock -> each group gets its own shipping date")
    void reeurder_withMixedStock_setsShippingDatePerGroup() {
        PostEurderRequest request
                = new PostEurderRequest(List.of(new PostItemGroupRequest(cup.getId(), 5),
                                                new PostItemGroupRequest(spoon.getId(), 1)));
        String originalId = postEurder(customerId, request);

        reeurder(customerId, originalId)
                .statusCode(201)
                .body("items", hasSize(2))
                .body("items.find { it.itemName == 'Cup' }.shippingDate",
                      equalTo(LocalDate.now().plusDays(7).toString()))
                .body("items.find { it.itemName == 'Spoon' }.shippingDate",
                      equalTo(LocalDate.now().plusDays(1).toString()));
    }

    // ---------- not found ----------

    @Test
    @DisplayName("Reeurder with unknown Eurder id -> 404")
    void reeurder_withUnknownEurderId_returns404() {
        reeurder(customerId, UUID.randomUUID().toString()).statusCode(404);
    }

    @Test
    @DisplayName("Reeurder with unknown Eurder id -> nothing is created and stock is untouched")
    void reeurder_withUnknownEurderId_changesNothing() {
        reeurder(customerId, UUID.randomUUID().toString()).statusCode(404);

        assertEquals(5, currentStock(cup));
        given().header(AUTHORIZATION, adminId)
               .when()
               .get(EURDERS)
               .then()
               .statusCode(200)
               .body("", hasSize(0));
    }

    @Test
    @DisplayName("Reeurder of another customer's Eurder -> 404")
    void reeurder_ofAnotherCustomersEurder_returns404() {
        String foreignId = postEurder(otherCustomerId, validRequest(cup.getId(), 1));

        reeurder(customerId, foreignId).statusCode(404);
    }

    @Test
    @DisplayName(
            "Reeurder of another customer's Eurder -> nothing is created and stock is untouched")
    void reeurder_ofAnotherCustomersEurder_changesNothing() {
        String foreignId = postEurder(otherCustomerId, validRequest(cup.getId(), 1));
        int stockBefore = currentStock(cup);

        reeurder(customerId, foreignId).statusCode(404);

        assertEquals(stockBefore, currentStock(cup));
        given().header(AUTHORIZATION, customerId)
               .when()
               .get(MY_EURDERS)
               .then()
               .statusCode(200)
               .body("", hasSize(0));
    }

    @Test
    @DisplayName("Reeurder as admin of a customer's Eurder -> 404")
    void reeurder_asAdminOfACustomersEurder_returns404() {
        String customersId = postEurder(customerId, validRequest(cup.getId(), 1));

        reeurder(adminId, customersId).statusCode(404);
    }

    @Test
    @DisplayName("Reeurder when the item no longer exists -> 404")
    void reeurder_whenItemWasRemoved_returns404() {
        String originalId = postEurder(customerId, validRequest(cup.getId(), 1));
        itemRepository.deleteWithId(cup.getId(), cup);

        reeurder(customerId, originalId).statusCode(404);
    }

    // ---------- bad input ----------

    @Test
    @DisplayName("Reeurder with a malformed Eurder id -> 400")
    void reeurder_withMalformedEurderId_returns400() {
        reeurder(customerId, "not-a-uuid").statusCode(400);
    }

    // ---------- authentication ----------

    @Test
    @DisplayName("Reeurder without Authorization header -> 401")
    void reeurder_withoutAuthorizationHeader_returns401() {
        String originalId = postEurder(customerId, validRequest(cup.getId(), 1));

        given().when().post(REEURDER, originalId).then().statusCode(401);
    }

    @Test
    @DisplayName("Reeurder with unknown user id -> 401")
    void reeurder_withUnknownUser_returns401() {
        String originalId = postEurder(customerId, validRequest(cup.getId(), 1));

        reeurder(UUID.randomUUID(), originalId).statusCode(401);
    }

    @Test
    @DisplayName("Reeurder without authorization -> nothing is created and stock is untouched")
    void reeurder_withoutAuthorization_changesNothing() {
        String originalId = postEurder(customerId, validRequest(cup.getId(), 1));
        int stockBefore = currentStock(cup);

        given().when().post(REEURDER, originalId).then().statusCode(401);

        assertEquals(stockBefore, currentStock(cup));
        given().header(AUTHORIZATION, adminId)
               .when()
               .get(EURDERS)
               .then()
               .statusCode(200)
               .body("", hasSize(1));
    }
}