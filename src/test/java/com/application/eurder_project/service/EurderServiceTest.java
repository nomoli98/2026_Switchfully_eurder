package com.application.eurder_project.service;

import com.application.eurder_project.domain.Item;
import com.application.eurder_project.domain.Eurder;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.repository.EurderRepository;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.service.validator.PostItemValidator;
import com.application.eurder_project.service.validator.PostEurderValidator;
import com.application.eurder_project.service.validator.PostUserValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EurderServiceTest {

    private final EurderRepository eurderRepository = new EurderRepository();
    private final ItemRepository itemRepository = new ItemRepository();
    private final UserRepository userRepository = new UserRepository();
    private final PostUserValidator postUserValidator = new PostUserValidator(userRepository);
    private final PostItemValidator postItemValidator = new PostItemValidator(itemRepository);
    private final PostEurderValidator postOrderValidator
            = new PostEurderValidator(postUserValidator,
                                      postItemValidator);
    private final EurderService service = new EurderService(eurderRepository,
                                                            itemRepository,
                                                            postOrderValidator);

    private final List<Eurder> eurders = ServiceTestData.validEurderList();
    private UUID firstItemId;
    private User admin;

    @BeforeEach
    void setUp() {
        Item firstItem = ServiceTestData.validItem();
        firstItemId = firstItem.getId();
        itemRepository.save(firstItem);
        admin = ServiceTestData.validAdmin();
        userRepository.save(admin);
    }

    @AfterEach
    void tearDown() {
        itemRepository.reset();
        userRepository.reset();
        eurderRepository.reset();
    }

    @Test
    @DisplayName("GetAll with an empty repo")
    void WhenFetchingAllEurdersOnAnEmptyRepo_ThenReturnsAnEmptyList() {
        assertEquals(0, service.getAllEurders().size());
    }

    @Test
    @DisplayName("GetAll with an filled repo")
    void WhenAListOfFourEurdersWasPreSaved_WhenFetchingAllEurders_ThenReturnsAListOfFour() {
        eurders.forEach(eurderRepository::save);
        assertEquals(4, service.getAllEurders().size());
    }

    @Test
    @DisplayName("GetById returns the order")
    void WhenFetchingOneEurderById_ReturnsSaidEurder() {
        Eurder eurder = eurderRepository.save(ServiceTestData.validEurder(admin));
        assertEquals(eurder, service.getOneEurderById(eurder.getId()));
    }

    @Test
    @DisplayName("GetByUser")
    void WhenASameCustomerMakes4Orders_WhenGetEurderByUser_ThenReturnsAListOf4() {
        List<Eurder> eurdersByTheSameUser = ServiceTestData.validEurderListByTheSameUser(admin, 4);
        eurdersByTheSameUser.forEach(eurderRepository::save);
        assertEquals(4, service.getEurdersByUser(admin.getId()).size());
    }

}