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

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Double.compare(price, item.price) == 0 && stock == item.stock &&
               Objects.equals(id, item.id) && Objects.equals(name, item.name) &&
               Objects.equals(description, item.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price, stock);
    }
}
