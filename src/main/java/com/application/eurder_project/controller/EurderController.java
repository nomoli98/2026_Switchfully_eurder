package com.application.eurder_project.controller;

import com.application.eurder_project.controller.dto.EurderDto;
import com.application.eurder_project.controller.dto.ShippedTodayDto;
import com.application.eurder_project.controller.mapper.EurderMapper;
import com.application.eurder_project.controller.mapper.ShippingTodayMapper;
import com.application.eurder_project.security.Permission;
import com.application.eurder_project.security.SecurityService;
import com.application.eurder_project.service.EurderService;
import com.application.eurder_project.service.request.PostEurderRequest;
import com.application.eurder_project.service.response.ShippedToday;
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
@RequestMapping("eurders")
public class EurderController {
    private final EurderService service;
    private final EurderMapper mapper;
    private final ShippingTodayMapper shippingTodayMapper;
    private final SecurityService security;
    private static final Logger logger = LoggerFactory.getLogger(EurderController.class);

    public EurderController(EurderService service,
                            EurderMapper mapper,
                            ShippingTodayMapper shippingTodayMapper,
                            SecurityService security) {
        this.service = service;
        this.mapper = mapper;
        this.shippingTodayMapper = shippingTodayMapper;
        this.security = security;
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping
    public List<EurderDto> getAllEurder(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID id) {
        security.validateAuthorization(id, Permission.VIEW_ALL_ORDERS);
        logger.info("Fetching all orders.");
        return mapper.toDtos(service.getAllEurders());
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping("user/{id}")
    public List<EurderDto> getEurdersByUser(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID id, @PathVariable("id") UUID userId) {
        security.validateAuthorization(id, Permission.VIEW_ORDERS_BY_USER);
        logger.info("Fetching all orders related to searched user.");
        return mapper.toDtos(service.getEurdersByUser(userId));
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping("my-orders")
    public List<EurderDto> getAllUserEurder(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID id) {
        security.validateAuthorization(id, Permission.VIEW_MY_ORDERS);
        logger.info("Fetching all your orders.");
        return mapper.toDtos(service.getEurdersByUser(id));
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping("my-orders/today")
    public ShippedTodayDto getEudersShippingToday(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID id) {
        security.validateAuthorization(id, Permission.VIEW_MY_ORDERS);
        logger.info("Fetching all your orders with items shipping today.");
        ShippedToday shippedToday = service.getOneUserItemsShippedToday(id);
        return shippingTodayMapper.toDto(shippedToday.items(), shippedToday.address());
    }

    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "Create an order")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Order created"),
                   @ApiResponse(responseCode = "401",
                                description = "Missing or invalid Authorization header"),
                   @ApiResponse(responseCode = "403", description = "Not allowed to create users")})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EurderDto createEurder(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID id, @RequestBody PostEurderRequest request) {
        security.validateAuthorization(id, Permission.CREATE_ORDER);
        logger.info("Creating a new order.");
        return mapper.toDto(service.createEurder(id, request));
    }
}
