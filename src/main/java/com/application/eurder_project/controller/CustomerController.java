package com.application.eurder_project.controller;

import com.application.eurder_project.controller.dto.CustomerDto;
import com.application.eurder_project.controller.mapper.CustomerMapper;
import com.application.eurder_project.service.CustomerService;
import com.application.eurder_project.service.request.PostCustomerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("customers")
public class CustomerController {
    private final CustomerService service;
    private final CustomerMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    public CustomerController(CustomerService service, CustomerMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<CustomerDto> getAllCustomers() {
        logger.info("Fetching all customers.");
        return mapper.toDtos(service.getAllCustomers());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto createCustomer(@RequestBody PostCustomerRequest request) {
        logger.info("Creating a new customer.");
        return mapper.toDto(service.createCustomer(request));
    }
}