package com.application.eurder_project.service;

import com.application.eurder_project.domain.Customer;
import com.application.eurder_project.repository.CustomerRepository;
import com.application.eurder_project.service.exception.RequestException;
import com.application.eurder_project.service.request.PostCustomerRequest;
import com.application.eurder_project.service.validator.CustomerValidator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private CustomerRepository repository;
    private CustomerValidator validator;

    public CustomerService() {
        this.repository = new CustomerRepository();
    }

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    public List<Customer> getAllCustomers() {
        return repository.getAll();
    }

    public Customer createCustomer(PostCustomerRequest request) {
        if (validator.isNotFilledIn(request.firstname())) throw RequestException.notFilledOutField("firstname");
        if (validator.isNotFilledIn(request.lastname())) throw RequestException.notFilledOutField("lastname");
        if (validator.isNotFilledIn(request.email())) throw RequestException.notFilledOutField("email");
        if (!validator.isValidEmail(request.email())) throw RequestException.invalidField("email");
        if (validator.isNotFilledIn(request.phoneNumber())) throw RequestException.notFilledOutField("phone number");
        if (!validator.isValidPhoneNumber(request.phoneNumber())) throw RequestException.invalidField("phone number");
        if (!validator.isValidAddress(request.address())) throw RequestException.invalidField("address");

        return repository.save(new Customer(
                request.firstname(),
                request.lastname(),
                request.email(),
                request.phoneNumber(),
                request.address()
        ));
    }
}
