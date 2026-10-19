package com.application.eurder_project.controller.mapper;

import com.application.eurder_project.controller.dto.CustomerDto;
import com.application.eurder_project.domain.Customer;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerMapper {
    public CustomerDto toDto(Customer customer) {
        return new CustomerDto(customer.getId(),
                customer.getFirstname(),
                customer.getLastname(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getAddress());
    }

    public List<CustomerDto> toDtos(List<Customer> customers){
        return customers.stream().map(this::toDto).toList();
    }
}
