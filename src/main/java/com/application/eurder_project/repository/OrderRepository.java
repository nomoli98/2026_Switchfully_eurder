package com.application.eurder_project.repository;

import com.application.eurder_project.domain.Order;

import java.util.List;

public class OrderRepository extends FakeDatabase<Order> {

    public OrderRepository() {}

    public OrderRepository(List<Order> orders) {
        orders.forEach(this::save);
    }

    public Order save(Order order) {
        return saveWithId(order.getId(), order);
    }
}
