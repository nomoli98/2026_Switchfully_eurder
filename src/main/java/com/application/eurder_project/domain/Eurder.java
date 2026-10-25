package com.application.eurder_project.domain;

import com.application.eurder_project.domain.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Eurder {
    private final UUID id;
    private final List<ItemGroup> items;
    private final User user;

    public Eurder(List<ItemGroup> items, User user) {
        this.id = UUID.randomUUID();
        this.items = items;
        this.user = user;
    }

    public UUID getId() {
        return id;
    }

    public List<ItemGroup> getItems() {
        return items;
    }

    public User getUser() {
        return user;
    }

    public double getTotalPrice() {
        return items.stream().mapToDouble(ItemGroup::getGroupPrice).sum();
    }

    public List<ItemGroup> getItemGroupsShippingOn(LocalDate date) {
        return items.stream()
                    .filter(itemGroup -> date.equals(itemGroup.getShippingDate()))
                    .toList();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((Eurder) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
