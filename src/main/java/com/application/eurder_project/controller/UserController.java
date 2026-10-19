package com.application.eurder_project.controller;

import com.application.eurder_project.controller.dto.UserDto;
import com.application.eurder_project.controller.mapper.UserMapper;
import com.application.eurder_project.service.UserService;
import com.application.eurder_project.service.request.PostUserRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("customers")
public class UserController {
    private final UserService service;
    private final UserMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService service, UserMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping
    public List<UserDto> getAllUser() {
        logger.info("Fetching all customers.");
        return mapper.toDtos(service.getAllUsers());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody PostUserRequest request) {
        logger.info("Creating a new customer.");
        return mapper.toDto(service.createUser(request));
    }
}