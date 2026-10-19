package com.application.eurder_project.service;

import com.application.eurder_project.controller.CustomerController;
import com.application.eurder_project.domain.Address;
import com.application.eurder_project.domain.Customer;
import com.application.eurder_project.repository.CustomerRepository;
import com.application.eurder_project.service.exception.RequestException;
import com.application.eurder_project.service.request.PostCustomerRequest;
import com.application.eurder_project.service.validator.CustomerValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository repository;
    private final CustomerValidator validator;
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    public CustomerService(CustomerRepository repository, CustomerValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public List<Customer> getAllCustomers() {
        return repository.getAll();
    }

    public Customer createCustomer(PostCustomerRequest request) {
        logger.info("Verifying inputs.");
        if (validator.isNotFilledIn(request.firstname())) throw RequestException.notFilledOutField("firstname");
        if (validator.isNotFilledIn(request.lastname())) throw RequestException.notFilledOutField("lastname");
        if (validator.isNotFilledIn(request.email())) throw RequestException.notFilledOutField("email");
        if (!validator.isValidEmail(request.email())) throw RequestException.invalidField("email");
        if (validator.isNotFilledIn(request.phoneNumber())) throw RequestException.notFilledOutField("phone number");
        if (!validator.isValidPhoneNumber(request.phoneNumber())) throw RequestException.invalidField("phone number");
        Address address = new Address(request.street(),
                                      request.number(),
                                      request.apartment(),
                                      request.postalCode(),
                                      request.country());
        if (!validator.isValidAddress(address)) throw RequestException.invalidField("address");

        Address address1 = address;
        return repository.save(new Customer(request.firstname(),
                                            request.lastname(),
                                            request.email(),
                                            request.phoneNumber(),
                                            address));
    }
}
