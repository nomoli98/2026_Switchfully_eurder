package com.application.eurder_project.repository;

import com.application.eurder_project.domain.Customer;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerRepository extends FakeDatabase {
    private final ConcurrentHashMap<UUID, Customer> customers;

    public CustomerRepository() {
        this.customers = new ConcurrentHashMap<>();
    }

    public CustomerRepository(List<Customer> customers) {
        this.customers = new ConcurrentHashMap<>();
        customers.forEach(this::save);
    }

    public Customer save(Customer customer){
        return customers.put(customer.getId(), customer);
    }
}
