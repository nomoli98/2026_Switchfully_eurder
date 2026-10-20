package com.application.eurder_project.controller;

import com.application.eurder_project.controller.dto.ItemDto;
import com.application.eurder_project.controller.mapper.ItemMapper;
import com.application.eurder_project.security.Permission;
import com.application.eurder_project.security.SecurityService;
import com.application.eurder_project.service.ItemService;
import com.application.eurder_project.service.request.PostItemRequest;
import com.application.eurder_project.service.request.PutItemRequest;
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
    public List<ItemDto> getAllItems() {
        logger.info("Fetching all items.");
        return mapper.toDtos(service.getAllItems());
    }

    @GetMapping(path = "{id}")
    public ItemDto getItemById(@PathVariable("id") UUID itemId) {
        logger.info("Fetching searched item.");
        return mapper.toDto(service.getItemById(itemId));
    }

    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "Create an item")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Item created"),
                   @ApiResponse(responseCode = "401",
                                description = "Missing or invalid Authorization header"),
                   @ApiResponse(responseCode = "403", description = "Not allowed to create users")})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto createUser(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID authorization, @RequestBody PostItemRequest request) {
        security.validateAuthorization(authorization, Permission.CREATE_ITEM);
        logger.info("Creating a new item.");
        return mapper.toDto(service.createItem(request));
    }

    @SecurityRequirement(name = "Authorization")
    @Operation(summary = "Update an item")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Item updated"),
                   @ApiResponse(responseCode = "400", description = "Invalid update body"),
                   @ApiResponse(responseCode = "401",
                                description = "Missing or invalid Authorization header"),
                   @ApiResponse(responseCode = "403", description = "Not allowed to update users")})
    @PutMapping(path = "{id}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto createUser(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID authorization,
            @RequestBody PutItemRequest request,
            @PathVariable("id") UUID itemId) {
        security.validateAuthorization(authorization, Permission.CREATE_ITEM);
        logger.info("Updating an item.");
        return mapper.toDto(service.updateItem(itemId, request));
    }


}
