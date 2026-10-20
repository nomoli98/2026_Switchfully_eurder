package com.application.eurder_project.service;

import com.application.eurder_project.domain.user.Address;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.request.PostUserRequest;
import com.application.eurder_project.service.validator.PostUserValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository repository;
    private final PostUserValidator validator;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserRepository repository, PostUserValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public List<User> getAllUsers() {
        return repository.getAll();
    }

    public User getUserById(UUID id) {
        return repository.get(id)
                         .orElseThrow(() -> ExistenceException.NotInDatabase(User.class.getTypeName()));
    }

    public User createUser(PostUserRequest request) {
        Address address = new Address(request.street(),
                                      request.number(),
                                      request.apartment(),
                                      request.postalCode(),
                                      request.country());
        logger.info("Verifying inputs.");
        validator.isValidRequest(request, address);
        logger.debug("Input validated.");
        return repository.save(new User(request.firstname(),
                                        request.lastname(),
                                        request.email(),
                                        request.phoneNumber(),
                                        address,
                                        request.role()));
    }
}
