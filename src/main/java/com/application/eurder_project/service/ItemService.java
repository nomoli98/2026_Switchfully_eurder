package com.application.eurder_project.service;

import com.application.eurder_project.domain.Item;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.request.PostItemRequest;
import com.application.eurder_project.service.request.PutItemRequest;
import com.application.eurder_project.service.validator.ItemValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.application.eurder_project.service.validator.ValidationUtils.isNotFilledIn;

@Service
public class ItemService {
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    private final ItemRepository repository;

    public ItemService(ItemRepository repository) {
        this.repository = repository;
    }

    public List<Item> getAllItems() {
        return repository.getAll();
    }

    public Item getItemById(UUID id) {
        return repository.get(id)
                         .orElseThrow(() -> ExistenceException.notInDatabase("item"));
    }

    public Item createItem(PostItemRequest request) {
        logger.info("Verifying post inputs.");
        ItemValidator.validateNewItem(request);
        logger.info("Post input validated.");
        return repository.save(new Item(request.name(),
                                        request.description(),
                                        request.price(),
                                        request.stock()));
    }

    public Item updateItem(UUID itemId, PutItemRequest request) {
        Item item = getItemById(itemId);
        logger.info("Verifying put inputs");
        ItemValidator.validateItemUpdate(request);
        logger.info("Put input validated.");
        applyUpdate(item, request);
        return repository.save(item);
    }

    /**
     * Only the fields that are filled in are changed, the id never changes.
     */
    private void applyUpdate(Item item, PutItemRequest request) {
        if (!isNotFilledIn(request.name())) item.setName(request.name());
        if (!isNotFilledIn(request.description())) item.setDescription(request.description());
        if (!isNotFilledIn(request.price())) item.setPrice(request.price());
        if (!isNotFilledIn(request.stock())) item.setStock(request.stock());
    }
}
