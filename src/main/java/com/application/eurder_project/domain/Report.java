package com.application.eurder_project.domain;


import java.util.List;

public class Report {
    private final List<Eurder> orders;
    private final double totalPriceForAllOrders;


    public Report(List<Eurder> orders) {
        this.orders = orders;
        this.totalPriceForAllOrders = calculateTotalPrice();
    }

    public List<Eurder> getOrders() {
        return orders;
    }

    public double getTotalPriceForAllOrders() {
        return totalPriceForAllOrders;
    }

    public double calculateTotalPrice() {
        return orders.stream().mapToDouble(Eurder::getTotalPrice).sum();
    }
}
