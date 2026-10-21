package com.application.eurder_project.controller.dto;

import java.util.List;

public class EurdersReportDto {
    private final List<EurderDto> eurderDtos;
    private final double totalPriceForAllOrders;


    public EurdersReportDto(List<EurderDto> eurderDtos) {
        this.eurderDtos = eurderDtos;
        this.totalPriceForAllOrders = calculateTotalPrice();
    }

    public double calculateTotalPrice(){
        return eurderDtos.stream()
                         .mapToDouble(EurderDto::totalPrice)
                         .sum();
    }

}
