package com.application.eurder_project.service;

import com.application.eurder_project.controller.UserController;
import com.application.eurder_project.domain.Address;
import com.application.eurder_project.domain.User;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.service.exception.RequestException;
import com.application.eurder_project.service.request.PostUserRequest;
import com.application.eurder_project.service.validator.CustomerValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository repository;
    private final CustomerValidator validator;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserService(UserRepository repository, CustomerValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public List<User> getAllUsers() {
        return repository.getAll();
    }

    public User createUser(PostUserRequest request) {
        logger.info("Verifying inputs.");
        if (validator.isNotFilledIn(request.firstname())) throw RequestException.notFilledOutField("firstname");
        if (validator.isNotFilledIn(request.lastname())) throw RequestException.notFilledOutField("lastname");
        if (validator.isNotFilledIn(request.email())) throw RequestException.notFilledOutField("email");
        if (!validator.isValidEmail(request.email())) throw RequestException.invalidField("email");
        if (validator.isNotFilledIn(request.phoneNumber())) throw RequestException.notFilledOutField("phone number");
        if (!validator.isValidPhoneNumber(request.phoneNumber())) throw RequestException.invalidField("phone number");
        Address address = new Address(request.street(),
                                      request.number(),
                                      request.apartment(),
                                      request.postalCode(),
                                      request.country());
        if (!validator.isValidAddress(address)) throw RequestException.invalidField("address");
        if (validator.isNotFilledIn(request.role())) throw RequestException.notFilledOutField("role");
        if (!validator.isValidRole(request.role())) throw RequestException.invalidField("role");


        Address address1 = address;
        return repository.save(new User(request.firstname(),
                                        request.lastname(),
                                        request.email(),
                                        request.phoneNumber(),
                                        address,
                                        request.role()));
    }
}
