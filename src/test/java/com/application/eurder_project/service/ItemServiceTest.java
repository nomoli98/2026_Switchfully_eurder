package com.application.eurder_project.service;

import com.application.eurder_project.domain.Item;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.exception.RequestException;
import com.application.eurder_project.service.request.PostItemRequest;
import com.application.eurder_project.service.request.PutItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ItemServiceTest {
    private final ItemRepository repository = new ItemRepository();
    private final ItemService service = new ItemService(repository);

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

    @Test
    @DisplayName("Update keeps the id and the stored item")
    void WhenUpdatingAnItem_ThenTheIdStaysTheSameAndTheStoredItemIsUpdated(){
        Item updated = service.updateItem(firstItemId, new PutItemRequest("Glass", null, null, null));
        assertEquals(firstItemId, updated.getId());
        assertEquals(5, service.getAllItems().size());
        assertEquals("Glass", repository.get(firstItemId).orElseThrow().getName());
    }

    @Test
    @DisplayName("Update with only a price -> only the price changes")
    void WhenUpdatingOnlyThePrice_ThenOnlyThePriceChanges(){
        service.updateItem(firstItemId, new PutItemRequest(null, null, 8.99, null));
        Item item = service.getItemById(firstItemId);
        assertEquals("Cup", item.getName());
        assertEquals("Object to pour liquids", item.getDescription());
        assertEquals(8.99, item.getPrice());
        assertEquals(5, item.getStock());
    }

    @Test
    @DisplayName("Update with blank name and description -> they are not changed")
    void WhenUpdatingWithBlankTexts_ThenTheTextsAreNotChanged(){
        service.updateItem(firstItemId, new PutItemRequest(" ", "", null, 3));
        Item item = service.getItemById(firstItemId);
        assertEquals("Cup", item.getName());
        assertEquals("Object to pour liquids", item.getDescription());
        assertEquals(3, item.getStock());
    }

    @Test
    @DisplayName("Update the stock to 0 is allowed")
    void WhenUpdatingTheStockToZero_ThenTheStockIsZero(){
        service.updateItem(firstItemId, new PutItemRequest(null, null, null, 0));
        assertEquals(0, service.getItemById(firstItemId).getStock());
    }

    @Test
    @DisplayName("Update with a price of 0 or a negative stock throws")
    void WhenUpdatingWithAZeroPriceOrNegativeStock_ThenThrows(){
        assertThrows(RequestException.class, () -> service.updateItem(firstItemId, new PutItemRequest(null, null, 0.0, null)));
        assertThrows(RequestException.class, () -> service.updateItem(firstItemId, new PutItemRequest(null, null, null, -1)));
        assertEquals(10.99, service.getItemById(firstItemId).getPrice());
    }

    @Test
    @DisplayName("Update with an unknown id throws")
    void WhenUpdatingAnUnknownItem_ThenThrows(){
        assertThrows(ExistenceException.class, () -> service.updateItem(UUID.randomUUID(), new PutItemRequest("Glass", null, null, null)));
    }

    @Test
    @DisplayName("Create item with a stock or price of 0 throws")
    void WhenCreatingAnItemWithZeroStockOrPrice_ThenThrows(){
        assertThrows(RequestException.class, () -> service.createItem(new PostItemRequest("Cup", "Pour", 1.0, 0)));
        assertThrows(RequestException.class, () -> service.createItem(new PostItemRequest("Cup", "Pour", 0.0, 1)));
    }
}
