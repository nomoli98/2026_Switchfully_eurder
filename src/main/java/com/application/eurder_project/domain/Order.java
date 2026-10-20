package com.application.eurder_project.domain;

import com.application.eurder_project.domain.user.User;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Order {
    private final UUID id;
    private double totalPrice;
    private List<ItemGroup> items;
    private final User user;

    public Order(List<ItemGroup> items, User user) {
        this.id = UUID.randomUUID();
        for (ItemGroup i : items) {
            this.totalPrice += i.getGroupPrice();
        }
        this.items = items;
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public List<ItemGroup> getItems() {
        return items;
    }

    public User getUser() {
        return user;
    }

    public double calculatePrice() {
        double totalPrice = 0;
        for (ItemGroup i : getItems()) {
            totalPrice += i.calculateGroupPrice();
        }
        return totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Double.compare(totalPrice, order.totalPrice) == 0 && Objects.equals(items, order.items) &&
               Objects.equals(user, order.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalPrice, items, user);
    }
}
