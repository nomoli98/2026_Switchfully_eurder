package com.application.eurder_project.service;

import com.application.eurder_project.domain.Eurder;
import com.application.eurder_project.domain.Item;
import com.application.eurder_project.domain.ItemGroup;
import com.application.eurder_project.domain.user.User;
import com.application.eurder_project.repository.EurderRepository;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.repository.UserRepository;
import com.application.eurder_project.security.UnauthorizedException;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.request.PostEurderRequest;
import com.application.eurder_project.service.request.PostItemGroupRequest;
import com.application.eurder_project.service.response.ShippedToday;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EurderServiceTest {
    private final EurderRepository eurderRepository = new EurderRepository();
    private final ItemRepository itemRepository = new ItemRepository();
    private final UserRepository userRepository = new UserRepository();
    private final EurderService service = new EurderService(eurderRepository,
                                                            itemRepository,
                                                            userRepository);
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
    @DisplayName("save and get from the repository -> returns the same eurder")
    void givenASavedEurder_WhenGettingItFromTheRepository_ReturnsEurder() {
        eurderRepository.save(eurder);
        assertEquals(eurder, eurderRepository.get(eurder.getId()).orElseThrow());
    }


    @Test
    @DisplayName("getByUser with valid id -> return list of eurders")
    void givenAValidUserId_WhenGetByUser_ReturnsListOfEurder() {
        List<Eurder> eurders = ServiceTestData.validEurderList(admin);
        eurders.forEach(eurderRepository::save);
        assertEquals(4, service.getEurdersByUser(admin.getId()).size());
    }

    @Test
    @DisplayName("with valid user & valid eurder -> when reeurder -> returns new eurder")
    void givenAValidUserIdAndAValidEurderId_WhenReEurdering_ReturnsNewEurder() {
        eurderRepository.save(eurder);
        assertEquals(admin.getId(),
                     service.reeurderAnExistingEurder(admin.getId(), eurder.getId())
                            .getUser()
                            .getId());
    }

    @Test
    @DisplayName("with invalid user & valid eurder -> when reeurder -> throws exception")
    void givenAnInvalidUserIdAndAValidEurderId_WhenReEurdering_ThrowsExistenceException() {
        eurderRepository.save(eurder);
        assertThrows(ExistenceException.class,
                     () -> service.reeurderAnExistingEurder(UUID.randomUUID(), eurder.getId()));
    }

    @Test
    @DisplayName("with valid user & invalid eurder -> when reeurder -> throws exception")
    void givenAValidUserIdAndAnInvalidEurderId_WhenReEurdering_ThrowsExistenceException() {
        eurderRepository.save(eurder);
        assertThrows(ExistenceException.class,
                     () -> service.reeurderAnExistingEurder(admin.getId(), UUID.randomUUID()));
    }

    @Test
    @DisplayName(
            "with valid user & valid eurder -> if item price changer -> when reeurder -> returns different total price")
    void givenAValidUserIdAndAValidEurderId_WhenReEurderingAndChangingThePriceOfAnItem_ReturnsNewEurderWithDifferentPrice() {
        eurderRepository.save(eurder);
        Item item = eurder.getItems().get(0).getItem();
        item.setPrice(0.99);
        assertNotEquals(eurder.getTotalPrice(),
                        service.reeurderAnExistingEurder(admin.getId(), eurder.getId())
                               .getTotalPrice());
    }

    @Test
    @DisplayName("createEurder with the same item twice -> stock is used up group after group")
    void givenTheSameItemTwice_WhenCreatingEurder_TheSecondGroupIsCheckedAgainstTheStockLeft() {
        Item cup = ServiceTestData.validItem();
        itemRepository.save(cup);
        PostEurderRequest request
                = new PostEurderRequest(List.of(new PostItemGroupRequest(cup.getId(), 3),
                                                new PostItemGroupRequest(cup.getId(), 3)));

        Eurder created = service.createEurder(admin.getId(), request);

        assertEquals(LocalDate.now().plusDays(1), created.getItems().get(0).getShippingDate());
        assertEquals(LocalDate.now().plusDays(7), created.getItems().get(1).getShippingDate());
        assertEquals(-1, cup.getStock());
    }

    @Test
    @DisplayName("createEurder above the stock -> backorder, the stock goes negative")
    void givenAnAmountAboveTheStock_WhenCreatingEurder_TheStockGoesNegative() {
        Item cup = ServiceTestData.validItem();
        itemRepository.save(cup);

        service.createEurder(admin.getId(),
                             new PostEurderRequest(List.of(new PostItemGroupRequest(cup.getId(),
                                                                                    8))));

        assertEquals(-3, cup.getStock());
    }

    @Test
    @DisplayName("createEurder with an unknown item -> throws and stock is untouched")
    void givenAnUnknownItem_WhenCreatingEurder_ThrowsAndLeavesTheStockAlone() {
        Item cup = ServiceTestData.validItem();
        itemRepository.save(cup);
        PostEurderRequest request
                = new PostEurderRequest(List.of(new PostItemGroupRequest(cup.getId(), 2),
                                                new PostItemGroupRequest(UUID.randomUUID(), 1)));

        assertThrows(ExistenceException.class, () -> service.createEurder(admin.getId(), request));
        assertEquals(5, cup.getStock());
    }

    @Test
    @DisplayName("createEurder with an unknown user -> throws unauthorized")
    void givenAnUnknownUser_WhenCreatingEurder_ThrowsUnauthorized() {
        Item cup = ServiceTestData.validItem();
        itemRepository.save(cup);
        PostEurderRequest request
                = new PostEurderRequest(List.of(new PostItemGroupRequest(cup.getId(), 1)));

        assertThrows(UnauthorizedException.class,
                     () -> service.createEurder(UUID.randomUUID(), request));
    }

    @Test
    @DisplayName("createEurder -> name and unit price are a snapshot of the item")
    void givenAnItemThatChangesAfterTheEurder_TheItemGroupKeepsTheOldNameAndPrice() {
        Item cup = ServiceTestData.validItem();
        itemRepository.save(cup);
        Eurder created = service.createEurder(admin.getId(),
                                              new PostEurderRequest(List.of(new PostItemGroupRequest(
                                                      cup.getId(),
                                                      2))));

        cup.setName("Glass");
        cup.setPrice(1.0);

        ItemGroup group = created.getItems().get(0);
        assertEquals("Cup", group.getItemName());
        assertEquals(10.99, group.getUnitPrice());
        assertEquals(2 * 10.99, group.getGroupPrice(), 0.001);
        assertEquals(2 * 10.99, created.getTotalPrice(), 0.001);
    }

    @Test
    @DisplayName("items shipped today -> customer sees own groups, admin sees one entry per eurder")
    void givenEurdersShippingToday_WhenFetchingThem_CustomerAndAdminViewsAreCorrect() {
        LocalDate today = LocalDate.now();
        User customer = ServiceTestData.validUserList().get(0);
        userRepository.save(customer);
        Item cup = ServiceTestData.validItem();
        eurderRepository.save(new Eurder(List.of(new ItemGroup(cup, 1, today),
                                                 new ItemGroup(cup, 1, today.plusDays(1))),
                                         customer));
        eurderRepository.save(new Eurder(List.of(new ItemGroup(cup, 2, today.plusDays(1))), admin));

        List<ShippedToday> all = service.getAllItemsShippedToday();
        ShippedToday mine = service.getMyItemsShippedToday(customer.getId());

        assertEquals(1, all.size());
        assertEquals(1, all.get(0).items().size());
        assertEquals(customer.getAddress(), all.get(0).address());
        assertEquals(1, mine.items().size());
        assertEquals(customer.getAddress(), mine.address());
        assertEquals(0, service.getMyItemsShippedToday(admin.getId()).items().size());
        assertThrows(ExistenceException.class,
                     () -> service.getMyItemsShippedToday(UUID.randomUUID()));
    }
}
