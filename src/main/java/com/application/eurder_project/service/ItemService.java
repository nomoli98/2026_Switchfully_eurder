package com.application.eurder_project.service;

import com.application.eurder_project.domain.Item;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.request.PostItemRequest;
import com.application.eurder_project.service.request.PutItemRequest;
import com.application.eurder_project.service.validator.PostItemValidator;
import com.application.eurder_project.service.validator.PutItemValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ItemService {
    private final ItemRepository repository;
    private final PostItemValidator postValidator;
    private final PutItemValidator putValidator;
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    public ItemService(ItemRepository repository, PostItemValidator validator,
                       PutItemValidator putValidator) {
        this.repository = repository;
        this.postValidator = validator;
        this.putValidator = putValidator;
    }

    public List<Item> getAllItems() {return repository.getAll();}

    public Item getItemById(UUID id) {
        return repository.get(id)
                         .orElseThrow(() -> ExistenceException.NotInDatabase(
                                 Item.class.getTypeName()));
    }

    public Item createItem(PostItemRequest request) {
        logger.info("Verifying post inputs.");
        postValidator.isValidRequest(request);
        logger.info("Post input validated.");
        return repository.save(new Item(request.name(),
                                        request.description(),
                                        request.price(),
                                        request.stock()));
    }

    public Item updateItem(UUID itemId, PutItemRequest request) {
        logger.info("Verifying put inputs");
        putValidator.isValidRequest(itemId, request);
        logger.info("Put input validated.");
        return repository.updateItem(itemId, getItemById(itemId), new Item(request.getName(),
                                                                           request.getDescription(),
                                                                           request.getPrice(),
                                                                           request.getStock()));
    }
}
