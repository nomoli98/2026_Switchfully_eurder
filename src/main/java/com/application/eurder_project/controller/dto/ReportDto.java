package com.application.eurder_project.controller.dto;

import java.util.List;

public class ReportDto {
    private final List<EurderDto> orders;
    private final double totalPriceForAllOrders;


    public ReportDto(List<EurderDto> orders) {
        this.orders = orders;
        this.totalPriceForAllOrders = calculateTotalPrice();
    }

    public double calculateTotalPrice(){
        return orders.stream()
                     .mapToDouble(EurderDto::totalPrice)
                     .sum();
    }

}
