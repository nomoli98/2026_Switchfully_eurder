package com.application.eurder_project.repository;

import com.application.eurder_project.domain.Order;
import com.application.eurder_project.domain.user.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class OrderRepository extends FakeDatabase<Order> {

    public OrderRepository() {}

    public OrderRepository(List<Order> orders) {
        orders.forEach(this::save);
    }

    public Order save(Order order) {
        return saveWithId(order.getId(), order);
    }

    public List<Order> getByUser(User user){
        return getAllOptionals(i -> i.getUser().equals(user));
    }
}
