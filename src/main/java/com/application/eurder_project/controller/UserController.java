package com.application.eurder_project.controller;

import com.application.eurder_project.controller.dto.UserDto;
import com.application.eurder_project.controller.mapper.UserMapper;
import com.application.eurder_project.security.Permission;
import com.application.eurder_project.security.SecurityService;
import com.application.eurder_project.service.UserService;
import com.application.eurder_project.service.request.PostUserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("users")
public class UserController {
    private final UserService service;
    private final UserMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final SecurityService security;

    public UserController(UserService service, UserMapper mapper, SecurityService security) {
        this.service = service;
        this.mapper = mapper;
        this.security = security;
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping
    public List<UserDto> getAllUser(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID authorization) {
        security.validateAuthorization(authorization, Permission.VIEW_ALL_USERS);
        logger.info("Fetching all users.");
        return mapper.toDtos(service.getAllUsers());
    }

    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "Create a user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "401",
                         description = "Missing or invalid Authorization header"),
            @ApiResponse(responseCode = "403", description = "Not allowed to create users")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID authorization,
            @RequestBody PostUserRequest request) {
        security.validateAuthorization(authorization, Permission.CREATE_USER);
        logger.info("Creating a new user.");
        return mapper.toDto(service.createUser(request));
    }

    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "Get a user by his id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "400", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Not allowed to view users")
    })
    @GetMapping(path = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID authorization,
            @PathVariable("id") UUID userId) {
        security.validateAuthorization(authorization, Permission.VIEW_USER_BY_ID);
        logger.info("Fetching searched user.");
        return mapper.toDto(service.getUserById(userId));
    }
}
