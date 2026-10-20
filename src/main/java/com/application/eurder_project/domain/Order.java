package com.application.eurder_project.domain;

import com.application.eurder_project.domain.user.User;

import java.util.List;
import java.util.Objects;

public class Order {
    private double totalPrice;
    private List<ItemGroup> items;
    private final User customer;

    public Order(double totalPrice, List<ItemGroup> items, User customer) {
        for (ItemGroup i : items) {
            this.totalPrice += i.getGroupPrice();
        }
        this.items = items;
        this.customer = customer;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public List<ItemGroup> getItems() {
        return items;
    }

    public User getCustomer() {
        return customer;
    }

    public double calculatePrice(){
        double totalPrice = 0;
        for (ItemGroup i : getItems()){
            totalPrice += i.calculateGroupPrice();
        }
        return totalPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Double.compare(totalPrice, order.totalPrice) == 0 && Objects.equals(items, order.items) &&
               Objects.equals(customer, order.customer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalPrice, items, customer);
    }
}
