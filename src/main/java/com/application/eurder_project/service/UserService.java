package com.application.eurder_project.service;

import com.application.eurder_project.domain.user.Address;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.service.exception.ConflictException;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.request.PostUserRequest;
import com.application.eurder_project.service.validator.UserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> getAllUsers() {
        return repository.getAll();
    }

    public User getUserById(UUID id) {
        return repository.get(id)
                         .orElseThrow(() -> ExistenceException.notInDatabase("user"));
    }

    public User createUser(PostUserRequest request) {
        logger.info("Verifying inputs.");
        UserValidator.validateNewUser(request);
        if (repository.existsByEmailIgnoreCase(request.email())) throw ConflictException.emailAlreadyInUse();
        logger.debug("Input validated.");
        Address address = new Address(request.street(),
                                      request.number(),
                                      request.city(),
                                      request.postalCode(),
                                      request.country());
        return repository.save(new User(request.firstname(),
                                        request.lastname(),
                                        request.email(),
                                        request.phoneNumber(),
                                        address,
                                        request.role()));
    }
}
