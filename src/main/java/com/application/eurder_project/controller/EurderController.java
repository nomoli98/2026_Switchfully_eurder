package com.application.eurder_project.controller;

import com.application.eurder_project.controller.dto.EurderDto;
import com.application.eurder_project.controller.dto.ShippedTodayDto;
import com.application.eurder_project.controller.mapper.EurderMapper;
import com.application.eurder_project.controller.mapper.ShippingTodayMapper;
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
@RequestMapping("eurders")
public class EurderController {
    private static final Logger logger = LoggerFactory.getLogger(EurderController.class);

    private final EurderService service;
    private final EurderMapper mapper;
    private final ShippingTodayMapper shippingTodayMapper;
    private final SecurityService security;

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
    @Operation(summary = "Get all orders (admin)")
    @GetMapping
    public List<EurderDto> getAllEurders(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID authorization) {
        security.validateAuthorization(authorization, Permission.VIEW_ALL_ORDERS);
        logger.info("Fetching all orders.");
        return mapper.toDtos(service.getAllEurders());
    }

    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "Get all orders of one user (admin)")
    @GetMapping("user/{id}")
    public List<EurderDto> getEurdersByUser(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID authorization, @PathVariable("id") UUID userId) {
        security.validateAuthorization(authorization, Permission.VIEW_ORDERS_BY_USER);
        logger.info("Fetching all orders related to searched user.");
        return mapper.toDtos(service.getEurdersByUser(userId));
    }

    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "Get all my orders")
    @GetMapping("my-eurders")
    public List<EurderDto> getMyEurders(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID authorization) {
        security.validateAuthorization(authorization, Permission.VIEW_MY_ORDERS);
        logger.info("Fetching all your orders.");
        return mapper.toDtos(service.getEurdersByUser(authorization));
    }

    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "Get my items that ship today (customer)")
    @GetMapping("my-eurders/today")
    public ShippedTodayDto getMyEurdersShippingToday(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID authorization) {
        security.validateAuthorization(authorization, Permission.VIEW_MY_ORDERS);
        logger.info("Fetching all your items shipping today.");
        return shippingTodayMapper.toDto(service.getMyItemsShippedToday(authorization));
    }

    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "Get the items of all orders that ship today, with their address (admin)")
    @GetMapping("shipping-today")
    public List<ShippedTodayDto> getAllEurdersShippingToday(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID authorization) {
        security.validateAuthorization(authorization, Permission.VIEW_ALL_SHIPPING_TODAY);
        logger.info("Fetching all items shipping today.");
        return shippingTodayMapper.toDtos(service.getAllItemsShippedToday());
    }

    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "Create an order")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Order created"),
                   @ApiResponse(responseCode = "400", description = "Invalid order"),
                   @ApiResponse(responseCode = "401",
                                description = "Missing or invalid Authorization header"),
                   @ApiResponse(responseCode = "403", description = "Not allowed to create orders"),
                   @ApiResponse(responseCode = "404", description = "Item not found")})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EurderDto createEurder(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID authorization, @RequestBody PostEurderRequest request) {
        security.validateAuthorization(authorization, Permission.CREATE_ORDER);
        logger.info("Creating a new order.");
        return mapper.toDto(service.createEurder(authorization, request));
    }

    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "Order again one of my previous orders")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Order created"),
                   @ApiResponse(responseCode = "401",
                                description = "Missing or invalid Authorization header"),
                   @ApiResponse(responseCode = "403", description = "Not allowed to create orders"),
                   @ApiResponse(responseCode = "404", description = "Order or item not found")})
    @PostMapping("my-eurders/re-eurder/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public EurderDto reeurderAPreviousEurder(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID authorization, @PathVariable("id") UUID eurderId) {
        security.validateAuthorization(authorization, Permission.CREATE_ORDER);
        logger.info("Reordering your previous order.");
        return mapper.toDto(service.reeurderAnExistingEurder(authorization, eurderId));
    }
}
