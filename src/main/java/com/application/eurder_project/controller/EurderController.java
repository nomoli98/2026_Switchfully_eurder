package com.application.eurder_project.controller;

import com.application.eurder_project.controller.dto.EurderDto;
import com.application.eurder_project.controller.mapper.EurderMapper;
import com.application.eurder_project.security.Permission;
import com.application.eurder_project.security.SecurityService;
import com.application.eurder_project.service.EurderService;
import com.application.eurder_project.service.request.PostEurderRequest;
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
@RequestMapping("orders")
public class EurderController {
    private final EurderService service;
    private final EurderMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(EurderController.class);
    private final SecurityService security;


    public EurderController(EurderService service, EurderMapper mapper, SecurityService security) {
        this.service = service;
        this.mapper = mapper;
        this.security = security;
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping
    public List<EurderDto> getAllOrder(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID id) {
        security.validateAuthorization(id, Permission.VIEW_ALL_EURDERS);
        logger.info("Fetching all orders.");
        return mapper.toDtos(service.getAllEurders());
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping(path = "user/{id}")
    public List<EurderDto> getAllOrderOfOneUser(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID id, @PathVariable("id") UUID userId) {
        security.validateAuthorization(id, Permission.VIEW_ALL_EURDERS_OF_ONE_USER);
        logger.info("Fetching all orders of user " + userId);
        return mapper.toDtos(service.getEurdersByUser(userId));
    }

    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "Create an order")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created"),
            @ApiResponse(responseCode = "401",
                         description = "Missing or invalid Authorization header"),
            @ApiResponse(responseCode = "403", description = "Not allowed to create users")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EurderDto createOrder(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID id,
            @RequestBody PostEurderRequest request) {
        security.validateAuthorization(id, Permission.CREATE_EURDER);
        logger.info("Creating a new order.");
        return mapper.toDto(service.createEurder(id, request));
    }
}
