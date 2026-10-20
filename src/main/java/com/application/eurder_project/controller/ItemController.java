package com.application.eurder_project.controller;

import com.application.eurder_project.controller.dto.ItemDto;
import com.application.eurder_project.controller.dto.UserDto;
import com.application.eurder_project.controller.mapper.ItemMapper;
import com.application.eurder_project.security.Permission;
import com.application.eurder_project.security.SecurityService;
import com.application.eurder_project.service.ItemService;
import com.application.eurder_project.service.request.PostItemRequest;
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
@RequestMapping("items")
public class ItemController {
    private final ItemService service;
    private final ItemMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(ItemController.class);
    private final SecurityService security;


    public ItemController(ItemService service, ItemMapper mapper, SecurityService security) {
        this.service = service;
        this.mapper = mapper;
        this.security = security;
    }

    @GetMapping
    public List<ItemDto> getAllItems(){
        return mapper.toDtos(service.getAllItems());
    }

    @SecurityRequirement(name="Authorization")
    @Operation(summary = "Create an item")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Item created"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid Authorization header"),
            @ApiResponse(responseCode = "403", description = "Not allowed to create users")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createUser(@Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false) UUID id,
                              @RequestBody PostItemRequest request) {
        security.validateAuthorization(id, Permission.CREATE_USER);
        logger.info("Creating a new user.");
        return mapper.toDto(service.createItem(request));
    }
}
