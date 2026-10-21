package com.application.eurder_project.service;

import com.application.eurder_project.domain.Eurder;
import com.application.eurder_project.domain.Report;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.EurderRepository;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.service.validator.PostEurderValidator;
import com.application.eurder_project.service.validator.PostItemValidator;
import com.application.eurder_project.service.validator.PostUserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReportServiceTest {
    private final EurderRepository eurderRepository = new EurderRepository();
    private final ItemRepository itemRepository = new ItemRepository();
    private final UserRepository userRepository = new UserRepository();
    private final PostUserValidator userValidator = new PostUserValidator(userRepository);
    private final PostItemValidator itemValidator = new PostItemValidator(itemRepository);
    private final PostEurderValidator validator = new PostEurderValidator(userValidator,
                                                                          itemValidator);
    private final EurderService eurderService = new EurderService(eurderRepository,
                                                                  itemRepository,
                                                                  userRepository,
                                                                  validator);
    private final ReportService service = new ReportService(eurderService);
    private User admin;
    private List<Eurder> eurders;
    private double totalPrice;

    @BeforeEach
    void setUp() {
        admin = ServiceTestData.validAdmin();
        userRepository.save(admin);
        eurders = ServiceTestData.validEurderList(admin);
        totalPrice = eurders.stream().mapToDouble(Eurder::getTotalPrice).sum();
        eurders.forEach(eurderRepository::save);
    }

    @Test
    @DisplayName("When creating a report -> total is correct")
    void givenGivenARepoFilled_whenCreatingReport_getReportWithCorrectTotal() {
        assertEquals(totalPrice, service.createReport(admin.getId()).getTotalPriceForAllOrders());
    }
}