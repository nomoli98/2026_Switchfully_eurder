package com.application.eurder_project.controller;

import com.application.eurder_project.domain.Role;
import com.application.eurder_project.domain.User;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.service.request.PostUserRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
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

import static com.application.eurder_project.controller.UserTestData.validRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {

    private static final String CUSTOMERS = "/customers";
    private static final String AUTHORIZATION = "Authorization";
    private User admin;
    private UUID adminId;

    @Autowired
    private UserRepository userRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        admin = UserTestData.validAdmin();
        adminId = admin.getId();
        userRepository.save(admin);
    }

    @AfterEach
    void tearDown() {
        RestAssured.reset();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("GetAll -> Only get admin")
    void getAllCustomers_whenNoCustomersExist_returnsEmptyList() {
        given().header(AUTHORIZATION, adminId)
               .when()
               .get(CUSTOMERS)
               .then()
               .statusCode(200)
               .contentType(ContentType.JSON)
               .body("", hasSize(1));
    }

    @Test
    @DisplayName("CreateCustomer -> 201 and Customer")
    void createCustomer_withValidRequest_returns201AndTheCreatedCustomer() {
        given().header(AUTHORIZATION, adminId)
               .contentType(ContentType.JSON)
               .body(validRequest())
               .when()
               .post(CUSTOMERS)
               .then()
               .statusCode(201)
               .body("id", notNullValue())
               .body("firstname", equalTo("Jane"))
               .body("lastname", equalTo("Doe"))
               .body("email", equalTo("jane.doe@example.com"))
               .body("phoneNumber", equalTo("0471234567"))
               .body("address.street", equalTo("Main Street"))
               .body("address.number", equalTo(12))
               .body("address.apartment", equalTo("2B"))
               .body("address.postalCode", equalTo("1000"))
               .body("address.country", equalTo("Belgium"));
    }

    private String postCustomer(PostUserRequest request) {
        return given().header(AUTHORIZATION, adminId)
                      .contentType(ContentType.JSON)
                      .body(request)
                      .when()
                      .post(CUSTOMERS)
                      .then()
                      .statusCode(201)
                      .extract()
                      .path("id");
    }

    @Test
    @DisplayName("CreateCustomers -> GetAll -> List of Customers")
    void createdCustomers_appearInTheCustomerList() {
        String janeId = postCustomer(validRequest("Jane", "jane@example.com"));
        String johnId = postCustomer(validRequest("John", "john@example.com"));

        given().when()
               .header(AUTHORIZATION, adminId)
               .get(CUSTOMERS)
               .then()
               .statusCode(200)
               .body("", hasSize(3))
               .body("id", containsInAnyOrder(janeId, johnId, adminId.toString()))
               .body("email", containsInAnyOrder("jane@example.com", "john@example.com", admin.getEmail()));
    }

    @Test
    @DisplayName("BadRequest -> Not Stored")
    void invalidRequest_isNotStored() {
        given().header(AUTHORIZATION, adminId)
               .contentType(ContentType.JSON)
               .body(new PostUserRequest(null,
                                         "Doe",
                                         "jane@example.com",
                                         "0471234567",
                                         "Main Street",
                                         12,
                                         "2B",
                                         "1000",
                                         "Belgium",
                                         Role.CUSTOMER))
               .when()
               .post(CUSTOMERS)
               .then()
               .statusCode(400);

        given().when().header(AUTHORIZATION, adminId).get(CUSTOMERS).then().body("$", hasSize(1));
    }

    static Stream<Arguments> invalidRequests() {
        String email = "jane.doe@example.com";
        String phone = "0471234567";

        return Stream.of(
                // firstname / lastname
                arguments("firstname is null",
                          new PostUserRequest(null,
                                              "Doe",
                                              email,
                                              phone,
                                              "Main Street",
                                              12,
                                              "2B",
                                              "1000",
                                              "Belgium",
                                              Role.CUSTOMER)),
                arguments("firstname is empty",
                          new PostUserRequest("",
                                              "Doe",
                                              email,
                                              phone,
                                              "Main Street",
                                              12,
                                              "2B",
                                              "1000",
                                              "Belgium",
                                              Role.CUSTOMER)),
                arguments("firstname is whitespace",
                          new PostUserRequest("   ",
                                              "Doe",
                                              email,
                                              phone,
                                              "Main Street",
                                              12,
                                              "2B",
                                              "1000",
                                              "Belgium",
                                              Role.CUSTOMER)),
                arguments("lastname is null",
                          new PostUserRequest("Jane",
                                              null,
                                              email,
                                              phone,
                                              "Main Street",
                                              12,
                                              "2B",
                                              "1000",
                                              "Belgium",
                                              Role.CUSTOMER)),
                arguments("lastname is empty",
                          new PostUserRequest("Jane",
                                              "",
                                              email,
                                              phone,
                                              "Main Street",
                                              12,
                                              "2B",
                                              "1000",
                                              "Belgium",
                                              Role.CUSTOMER)),

                // email
                arguments("email is null",
                          new PostUserRequest("Jane",
                                              "Doe",
                                              null,
                                              phone,
                                              "Main Street",
                                              12,
                                              "2B",
                                              "1000",
                                              "Belgium",
                                              Role.CUSTOMER)),
                arguments("email is empty",
                          new PostUserRequest("Jane",
                                              "Doe",
                                              "",
                                              phone,
                                              "Main Street",
                                              12,
                                              "2B",
                                              "1000",
                                              "Belgium",
                                              Role.CUSTOMER)),
                arguments("email has no @",
                          new PostUserRequest("Jane",
                                              "Doe",
                                              "jane.doe.example.com",
                                              phone,
                                              "Main Street",
                                              12,
                                              "2B",
                                              "1000",
                                              "Belgium",
                                              Role.CUSTOMER)),
                arguments("email has no domain ext",
                          new PostUserRequest("Jane",
                                              "Doe",
                                              "jane@example",
                                              phone,
                                              "Main Street",
                                              12,
                                              "2B",
                                              "1000",
                                              "Belgium",
                                              Role.CUSTOMER)),
                arguments("email has no local part",
                          new PostUserRequest("Jane",
                                              "Doe",
                                              "@example.com",
                                              phone,
                                              "Main Street",
                                              12,
                                              "2B",
                                              "1000",
                                              "Belgium",
                                              Role.CUSTOMER)),

                // phone number
                arguments("phone is null",
                          new PostUserRequest("Jane",
                                              "Doe",
                                              email,
                                              null,
                                              "Main Street",
                                              12,
                                              "2B",
                                              "1000",
                                              "Belgium",
                                              Role.CUSTOMER)),
                arguments("phone is too short",
                          new PostUserRequest("Jane",
                                              "Doe",
                                              email,
                                              "047123456",
                                              "Main Street",
                                              12,
                                              "2B",
                                              "1000",
                                              "Belgium",
                                              Role.CUSTOMER)),
                arguments("phone is too long",
                          new PostUserRequest("Jane",
                                              "Doe",
                                              email,
                                              "04712345678",
                                              "Main Street",
                                              12,
                                              "2B",
                                              "1000",
                                              "Belgium",
                                              Role.CUSTOMER)),
                arguments("phone contains letters",
                          new PostUserRequest("Jane",
                                              "Doe",
                                              email,
                                              "04712345ab",
                                              "Main Street",
                                              12,
                                              "2B",
                                              "1000",
                                              "Belgium",
                                              Role.CUSTOMER)),

                // address
                arguments("street is null",
                          new PostUserRequest("Jane",
                                              "Doe",
                                              email,
                                              phone,
                                              null,
                                              12,
                                              "2B",
                                              "1000",
                                              "Belgium",
                                              Role.CUSTOMER)),
                arguments("street number is 0",
                          new PostUserRequest("Jane",
                                              "Doe",
                                              email,
                                              phone,
                                              "Main Street",
                                              0,
                                              "2B",
                                              "1000",
                                              "Belgium",
                                              Role.CUSTOMER)),
                arguments("postal code is null",
                          new PostUserRequest("Jane",
                                              "Doe",
                                              email,
                                              phone,
                                              "Main Street",
                                              12,
                                              "2B",
                                              null,
                                              "Belgium",
                                              Role.CUSTOMER)),
                arguments("country is null",
                          new PostUserRequest("Jane",
                                              "Doe",
                                              email,
                                              phone,
                                              "Main Street",
                                              12,
                                              "2B",
                                              "1000",
                                              null,
                                              Role.CUSTOMER)));
    }

    @ParameterizedTest(name = "{0} -> 400")
    @MethodSource("invalidRequests")
    @DisplayName("Invalid Request fields -> 400")
    void createCustomer_withInvalidRequest_returns400(String description, PostUserRequest request) {
        given().header(AUTHORIZATION, adminId)
               .contentType(ContentType.JSON)
               .body(request)
               .when()
               .post(CUSTOMERS)
               .then()
               .statusCode(400);
    }

}