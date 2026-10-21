package com.application.eurder_project.controller.dto;

import com.application.eurder_project.domain.Order;

import java.util.List;

public class OrdersReportDto {
    private final List<OrderDto> orders;
    private final double totalPriceForAllOrders;


    public OrdersReportDto(List<OrderDto> orders) {
        this.orders = orders;
        this.totalPriceForAllOrders = calculateTotalPrice();
    }

    public double calculateTotalPrice(){
        return orders.stream()
                     .mapToDouble(OrderDto::totalPrice)
                     .sum();
    }

}
