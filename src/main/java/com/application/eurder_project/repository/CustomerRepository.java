package com.application.eurder_project.repository;

import com.application.eurder_project.domain.Customer;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerRepository extends FakeDatabase<Customer> {

    public CustomerRepository() {
    }

    public CustomerRepository(List<Customer> customers) {
        customers.forEach(this::save);
    }

    public Customer save(Customer customer) {
        return saveWithId(customer.getId(), customer);
    }
}