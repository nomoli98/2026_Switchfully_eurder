package com.application.eurder_project.service;

import com.application.eurder_project.domain.user.Address;
import com.application.eurder_project.domain.user.Role;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.validator.PostUserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private final UserRepository repository = new UserRepository();
    private final PostUserValidator validator = new PostUserValidator(repository);
    private final UserService service = new UserService(repository, validator);
    private User admin;

    @BeforeEach
    void setUp() {
        admin = new User("Tom",
                              "Mayo",
                              "tom@sfth.uk",
                              "0123456788",
                              new Address("Shropshire Road",
                                          8,
                                          "3b",
                                          "2034",
                                          "United Kingdom"),
                              Role.ADMIN);
        List<User> users = List.of(new User("Alexander",
                                            "Jeremy",
                                            "aj@sfth.uk",
                                            "0123456789",
                                            new Address("Shropshire Road",
                                                        8,
                                                        "1a",
                                                        "2034",
                                                        "United Kingdom"),
                                            Role.CUSTOMER),
                                   new User("Luke",
                                            "Manning",
                                            "luke@sfth.uk",
                                            "0123456784",
                                            new Address("Shropshire Road",
                                                        8,
                                                        "1a",
                                                        "2034",
                                                        "United Kingdom"),
                                            Role.CUSTOMER),
                                   new User("Sam",
                                            "Russel",
                                            "sam@sfth.uk",
                                            "0123456786",
                                            new Address("Shropshire Road",
                                                        8,
                                                        "4a",
                                                        "2034",
                                                        "United Kingdom"),
                                            Role.CUSTOMER)
                                  );
        repository.save(admin);
        users.forEach(repository::save);
    }

    @Test
    @DisplayName("Checking getAll")
    void WhenAListOfFourUsersWasPreSaved_WhenFetchingAllUsers_ThenReturnsAListOfFour(){
        assertEquals(4, service.getAllUsers().size());
    }

    @Test
    @DisplayName("Checking getOne with valid id")
    void WhenAListOfFourUsersWasPreSaved_WhenFetchingAUserWithAValidId_ThenReturnsTheUser(){
        assertEquals("Tom", service.getUserById(admin.getId()).getFirstname());
    }

    @Test
    @DisplayName("Checking getOne with invalid id")
    void WhenAListOfFourUsersWasPreSaved_WhenFetchingAUserWithAInvalidId_ThenThrows(){
        assertThrows(ExistenceException.class, () -> service.getUserById(UUID.randomUUID()));
    }
}