package com.application.eurder_project.controller.dto;

import java.util.List;

public record ReportDto(List<EurderDto> eurderDtos, double totalPriceForAllOrders) {

}
