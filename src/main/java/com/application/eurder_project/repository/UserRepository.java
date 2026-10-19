package com.application.eurder_project.repository;

import com.application.eurder_project.domain.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository extends FakeDatabase<User> {

    public UserRepository() {
    }

    public UserRepository(List<User> customers) {
        customers.forEach(this::save);
    }

    public User save(User customer) {
        return saveWithId(customer.getId(), customer);
    }
}