package com.application.eurder_project.domain;

import java.util.Objects;
import java.util.UUID;

public class Item {
    private final UUID id;
    private String name;
    private String description;
    private double price;
    private int stock;

    public Item(String name, String description, double price, int stock) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean hasStockFor(int amount) {
        return stock >= amount;
    }

    // Backorder behaviour: the stock is allowed to become negative.
    // If it needs to change : replace the body with: stock = Math.max(0, stock - amount);
    public void removeStock(int amount) {
        stock -= amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(id, ((Item) o).id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
