package com.application.eurder_project.service;

import com.application.eurder_project.domain.Eurder;
import com.application.eurder_project.domain.Item;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.EurderRepository;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.validator.PostEurderValidator;
import com.application.eurder_project.service.validator.PostItemValidator;
import com.application.eurder_project.service.validator.PostUserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EurderServiceTest {
    private final EurderRepository eurderRepository = new EurderRepository();
    private final ItemRepository itemRepository = new ItemRepository();
    private final UserRepository userRepository = new UserRepository();
    private final PostUserValidator userValidator = new PostUserValidator(userRepository);
    private final PostItemValidator itemValidator = new PostItemValidator(itemRepository);
    private final PostEurderValidator validator = new PostEurderValidator(userValidator,
                                                                          itemValidator);
    private final EurderService service = new EurderService(eurderRepository,
                                                            itemRepository,
                                                            userRepository,
                                                            validator);
    private User admin;
    private Eurder eurder;

    @BeforeEach
    void setUp() {
        admin = ServiceTestData.validAdmin();
        userRepository.save(admin);
        eurder = ServiceTestData.validEurder(admin);
        eurder.getItems().forEach(group -> itemRepository.save(group.getItem()));
    }

    @Test
    @DisplayName("getAll when 4 in repo -> returns 4")
    void givenARepoFilledWith4Eurders_WhenGetAll_ReturnsAListOf4() {
        List<Eurder> eurders = ServiceTestData.validEurderList(admin);
        eurders.forEach(eurderRepository::save);
        assertEquals(4, service.getAllEurders().size());
    }

    @Test
    @DisplayName("getAll when 0 in repo -> returns 0")
    void givenAnEmptyRepo_WhenGetAll_ReturnsAnEmptyList() {
        assertEquals(0, service.getAllEurders().size());
    }

    @Test
    @DisplayName("getOne with valid id -> return eurder")
    void givenAValidId_WhenGetByID_ReturnsEurder(){
        eurderRepository.save(eurder);
        assertEquals(eurder.getUser(), service.getOneEurderById(eurder.getId()).getUser());
    }


    @Test
    @DisplayName("getByUser with valid id -> return list of eurders")
    void givenAValidUserId_WhenGetByUser_ReturnsListOfEurder(){
        List<Eurder> eurders = ServiceTestData.validEurderList(admin);
        eurders.forEach(eurderRepository::save);
        assertEquals(4, service.getEurdersByUser(admin.getId()).size());
    }

    @Test
    @DisplayName("with valid user & valid eurder -> when reeurder -> returns new eurder")
    void givenAValidUserIdAndAValidEurderId_WhenReEurdering_ReturnsNewEurder(){
        eurderRepository.save(eurder);
        assertEquals(admin.getId(), service.reeurderAnExistingEurder(admin.getId(), eurder.getId()).getUser().getId());
    }

    @Test
    @DisplayName("with invalid user & valid eurder -> when reeurder -> throws exception")
    void givenAnInvalidUserIdAndAValidEurderId_WhenReEurdering_ThrowsExistenceException(){
        eurderRepository.save(eurder);
        assertThrows(ExistenceException.class, () -> service.reeurderAnExistingEurder(UUID.randomUUID(), eurder.getId()));
    }

    @Test
    @DisplayName("with valid user & invalid eurder -> when reeurder -> throws exception")
    void givenAValidUserIdAndAnInvalidEurderId_WhenReEurdering_ThrowsExistenceException(){
        eurderRepository.save(eurder);
        assertThrows(ExistenceException.class, () -> service.reeurderAnExistingEurder(admin.getId(), UUID.randomUUID()));
    }

    @Test
    @DisplayName("with valid user & valid eurder -> if item price changer -> when reeurder -> returns different total price")
    void givenAValidUserIdAndAValidEurderId_WhenReEurderingAndChangingThePriceOfAnItem_ReturnsNewEurderWithDifferentPrice(){
        eurderRepository.save(eurder);
        Item item = eurder.getItems().get(0).getItem();
        itemRepository.updateItem(item.getId(), item, new Item(item.getName(), item.getDescription(), 0.99, item.getStock()));
        assertNotEquals(eurder.getTotalPrice(), service.reeurderAnExistingEurder(admin.getId(), eurder.getId()).getTotalPrice());
    }

}