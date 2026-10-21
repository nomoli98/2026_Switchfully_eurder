package com.application.eurder_project.controller.mapper;

import com.application.eurder_project.controller.dto.ReportDto;
import com.application.eurder_project.domain.Report;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReportMapper {

    private final EurderMapper eurderMapper;

    public ReportMapper(EurderMapper eurderMapper) {this.eurderMapper = eurderMapper;}

    public ReportDto toDto(Report report){
        return new ReportDto(eurderMapper.toDtos(report.getOrders()),
                             report.getTotalPriceForAllOrders());
    }

    public List<ReportDto> toDtos(List<Report> reports){
        return reports.stream().map(this::toDto).toList();
    }
}
