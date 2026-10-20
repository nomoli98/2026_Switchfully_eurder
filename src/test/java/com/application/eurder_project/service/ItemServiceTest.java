package com.application.eurder_project.service;

import com.application.eurder_project.domain.Item;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.service.exception.ExistenceException;
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
        Item firstItem = new Item("Cup", "Object to pour liquids", 10.99, 5);
        List<Item> items = List.of(new Item("Fork", "Object to stab solids", 5.99, 10),
                                   new Item("Knife", "Object to cut solids", 5.99, 5),
                                   new Item("Spoon", "Object to scoop liquids", 3.99, 10));
        firstItemId = firstItem.getId();
        repository.save(firstItem);
        items.forEach(repository::save);
    }

    @Test
    @DisplayName("Checkin getAll")
    void WhenAListOfFourItemsWasPreSaved_WhenFetchingAllItems_ThenReturnsAListOfFour() {
        assertEquals(4, service.getAllItems().size());
    }

    @Test
    @DisplayName("Checking getOne with valid id")
    void WhenAListOfFourItemsWasPreSaved_WhenFetchingAItemWithAValidId_ThenReturnsTheItem(){
        assertEquals("Cup", service.getItemById(firstItemId).getName());
    }

    @Test
    @DisplayName("Checking getOne with invalid id")
    void WhenAListOfFourItemsWasPreSaved_WhenFetchingAItemWithAInvalidId_ThenThrows(){
        assertThrows(ExistenceException.class, () -> service.getItemById(UUID.randomUUID()));
    }


}