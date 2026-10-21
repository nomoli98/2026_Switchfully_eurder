package com.application.eurder_project.service;

import com.application.eurder_project.domain.Eurder;
import com.application.eurder_project.domain.Report;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReportService {

    private final EurderService eurderService;

    public ReportService(EurderService eurderService) {this.eurderService = eurderService;}

    public Report createReport(UUID userId){
      List<Eurder> eurders = eurderService.getEurdersByUser(userId);
      return new Report(eurders);
    }
}
