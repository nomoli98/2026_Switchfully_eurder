package com.application.eurder_project.domain;

import java.util.List;

public class Report {
    private final List<Eurder> orders;

    public Report(List<Eurder> orders) {
        this.orders = orders;
    }

    public List<Eurder> getOrders() {
        return orders;
    }

    public double getTotalPriceForAllOrders() {
        return orders.stream().mapToDouble(Eurder::getTotalPrice).sum();
    }
}
