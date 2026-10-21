package com.application.eurder_project.domain;

import com.application.eurder_project.domain.user.User;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Eurder {
    private final UUID id;
    private double totalPrice;
    private List<ItemGroup> items;
    private final User user;

    public Eurder(List<ItemGroup> items, User user) {
        this.id = UUID.randomUUID();
        this.totalPrice = items.stream()
                                .mapToDouble(ItemGroup::getGroupPrice)
                                .sum();
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Eurder eurder = (Eurder) o;
        return Double.compare(totalPrice, eurder.totalPrice) == 0 && Objects.equals(items, eurder.items) &&
               Objects.equals(user, eurder.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalPrice, items, user);
    }
}
