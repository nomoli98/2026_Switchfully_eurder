package com.application.eurder_project.controller;

import com.application.eurder_project.controller.dto.ReportDto;
import com.application.eurder_project.controller.mapper.ReportMapper;
import com.application.eurder_project.security.Permission;
import com.application.eurder_project.security.SecurityService;
import com.application.eurder_project.service.ReportService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("reports")
public class ReportController {
    private final ReportService service;
    private final ReportMapper mapper;
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
    private final SecurityService security;

    public ReportController(ReportService service, ReportMapper mapper, SecurityService security) {
        this.service = service;
        this.mapper = mapper;
        this.security = security;
    }

    @SecurityRequirement(name = "Authorization")
    @GetMapping
    public ReportDto createReport(
            @Parameter(hidden = true) @RequestHeader(value = "Authorization", required = false)
            UUID id) {
        security.validateAuthorization(id, Permission.VIEW_MY_REPORT);
        return mapper.toDto(service.createReport(id));
    }


}
