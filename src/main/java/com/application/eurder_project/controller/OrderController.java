package com.application.eurder_project.controller;

import com.application.eurder_project.controller.dto.OrderDto;
import com.application.eurder_project.controller.mapper.OrderMapper;
import com.application.eurder_project.security.Permission;
import com.application.eurder_project.security.SecurityService;
import com.application.eurder_project.service.OrderService;
import com.application.eurder_project.service.request.PostOrderRequest;
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
public class OrderController {
    private final OrderService service;
    private final OrderMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final SecurityService security;


    public OrderController(OrderService service, OrderMapper mapper, SecurityService security) {
        this.service = service;
        this.mapper = mapper;
        this.security = security;
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping
    public List<OrderDto> getAllOrder(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) UUID id) {
        security.validateAuthorization(id, Permission.VIEW_ALL_ORDERS);
        logger.info("Fetching all orders.");
        return mapper.toDtos(service.getAllOrders());
    }

    @SecurityRequirement(name="Authorization")
    @Operation(summary = "Create an order")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Order created"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Authorization header"),
            @ApiResponse(responseCode = "403", description = "Not allowed to create users")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderDto createOrder(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) UUID id,
                                @RequestBody PostOrderRequest request) {
        security.validateAuthorization(id, Permission.CREATE_ORDER);
        logger.info("Creating a new order.");
        return mapper.toDto(service.createOrder(id, request));
    }
}
