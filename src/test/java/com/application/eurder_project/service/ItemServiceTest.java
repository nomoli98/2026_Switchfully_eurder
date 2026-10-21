package com.application.eurder_project.service;

import com.application.eurder_project.domain.Item;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.request.PutItemRequest;
import com.application.eurder_project.service.validator.PostItemValidator;
import com.application.eurder_project.service.validator.PutItemValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ItemServiceTest {
    private final ItemRepository repository = new ItemRepository();
    private final PostItemValidator postValidator = new PostItemValidator(repository);
    private final PutItemValidator putValidator = new PutItemValidator(repository);
    private final ItemService service = new ItemService(repository, postValidator, putValidator);

    private UUID firstItemId;

    @BeforeEach
    void setUp() {
        Item firstItem = ServiceTestData.validItem();
        List<Item> items = ServiceTestData.validItemList();
        firstItemId = firstItem.getId();
        repository.save(firstItem);
        items.forEach(repository::save);
    }

    @Test
    @DisplayName("GetAll")
    void WhenAListOfFiveItemsWasPreSaved_WhenFetchingAllItems_ThenReturnsAListOfFive() {
        assertEquals(5, service.getAllItems().size());
    }

    @Test
    @DisplayName("GetOne with valid id")
    void WhenAListOfFourItemsWasPreSaved_WhenFetchingAItemWithAValidId_ThenReturnsTheItem(){
        assertEquals("Cup", service.getItemById(firstItemId).getName());
    }

    @Test
    @DisplayName("GetOne with invalid id")
    void WhenAListOfFourItemsWasPreSaved_WhenFetchingAItemWithAInvalidId_ThenThrows(){
        assertThrows(ExistenceException.class, () -> service.getItemById(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Update with a valid id and request with one filled in field")
    void WhenWhenAListOfFourItemsWasPreSaved_WhenUpdatingAnItemWithAValidIdAnRequestWithOneFilledInField_ThenTheItemIsUpdated(){
        PutItemRequest request = new PutItemRequest("Glass", null, null, null);
        service.updateItem(firstItemId, request);
        assertEquals("Glass", service.getItemById(firstItemId).getName());
        assertEquals("Object to pour liquids", service.getItemById(firstItemId).getDescription());
        assertEquals(10.99, service.getItemById(firstItemId).getPrice());
        assertEquals(5, service.getItemById(firstItemId).getStock());
    }

    @Test
    @DisplayName("Update with a valid id and request with two filled in fields")
    void WhenWhenAListOfFourItemsWasPreSaved_WhenUpdatingAnItemWithAValidIdAnRequestWithTwoFilledInFields_ThenTheItemIsUpdated(){
        PutItemRequest request = new PutItemRequest("Glass", null, 8.99, null);
        service.updateItem(firstItemId, request);
        assertEquals("Glass", service.getItemById(firstItemId).getName());
        assertEquals("Object to pour liquids", service.getItemById(firstItemId).getDescription());
        assertEquals(8.99, service.getItemById(firstItemId).getPrice());
        assertEquals(5, service.getItemById(firstItemId).getStock());
    }

    @Test
    @DisplayName("Update with a valid id and request with all fields filled in")
    void WhenWhenAListOfFourItemsWasPreSaved_WhenUpdatingAnItemWithAValidIdAnRequestWithAllFieldsFilledIn_ThenTheItemIsUpdated(){
        PutItemRequest request = new PutItemRequest("Glass", "A glass, dummy", 0.99, 1);
        service.updateItem(firstItemId, request);
        assertEquals("Glass", service.getItemById(firstItemId).getName());
        assertEquals("A glass, dummy", service.getItemById(firstItemId).getDescription());
        assertEquals(0.99, service.getItemById(firstItemId).getPrice());
        assertEquals(1, service.getItemById(firstItemId).getStock());
    }
}