package com.application.eurder_project.controller;

import com.application.eurder_project.controller.dto.CustomerDto;
import com.application.eurder_project.controller.mapper.CustomerMapper;
import com.application.eurder_project.service.CustomerService;
import com.application.eurder_project.service.request.PostCustomerRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("customers")
public class CustomerController {
    private final CustomerService service;
    private final CustomerMapper mapper;

    public CustomerController() {
        this.mapper = new CustomerMapper();
        this.service = new CustomerService();
    }

    public CustomerController(CustomerService service, CustomerMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping
    public List<CustomerDto> getAllCustomers (){
        return mapper.toDtos(service.getAllCustomers());
    }

    @PostMapping
    public CustomerDto createCustomer(PostCustomerRequest request){
        return mapper.toDto(service.createCustomer(request));
    }

}
