package com.application.eurder_project.service;

import com.application.eurder_project.domain.user.Role;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.service.exception.ConflictException;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.exception.RequestException;
import com.application.eurder_project.service.request.PostUserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserServiceTest {

    private final UserRepository repository = new UserRepository();
    private final UserService service = new UserService(repository);
    private User admin;

    @BeforeEach
    void setUp() {
        admin = ServiceTestData.validAdmin();
        List<User> users = ServiceTestData.validUserList();
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

    private PostUserRequest request(String email, String phoneNumber) {
        return new PostUserRequest("Jane", "Doe", email, phoneNumber,
                                   "Main Street", 12, "London", "1000", "Belgium", Role.CUSTOMER);
    }

    @Test
    @DisplayName("Create user -> stored with the city in the address")
    void WhenCreatingAUser_ThenItIsStoredWithItsCity(){
        User created = service.createUser(request("jane@example.com", "0471234567"));
        assertEquals("London", created.getAddress().city());
        assertEquals(5, service.getAllUsers().size());
    }

    @Test
    @DisplayName("Create user with an email that is already used -> throws, whatever the case")
    void WhenCreatingAUserWithAnExistingEmail_ThenThrowsConflict(){
        assertThrows(ConflictException.class, () -> service.createUser(request("tom@sfth.uk", "0471234567")));
        assertThrows(ConflictException.class, () -> service.createUser(request("TOM@SFTH.UK", "0471234567")));
        assertEquals(4, service.getAllUsers().size());
    }

    @Test
    @DisplayName("Create user with a phone number with separators or a country code -> accepted")
    void WhenCreatingAUserWithAFormattedPhoneNumber_ThenItIsAccepted(){
        service.createUser(request("a@example.com", "+32 470 12 34 56"));
        service.createUser(request("b@example.com", "0470-12.34.56"));
        service.createUser(request("c@example.com", "003212345678"));
        assertEquals(7, service.getAllUsers().size());
    }

    @Test
    @DisplayName("Create user with an invalid phone number -> throws")
    void WhenCreatingAUserWithAnInvalidPhoneNumber_ThenThrows(){
        assertThrows(RequestException.class, () -> service.createUser(request("a@example.com", "04712")));
        assertThrows(RequestException.class, () -> service.createUser(request("a@example.com", "0471234567890123")));
        assertThrows(RequestException.class, () -> service.createUser(request("a@example.com", "04712345ab")));
        assertThrows(RequestException.class, () -> service.createUser(request("a@example.com", "+")));
    }
}
