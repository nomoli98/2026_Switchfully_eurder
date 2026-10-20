package com.application.eurder_project.service;

import com.application.eurder_project.domain.Item;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.service.request.PostItemRequest;
import com.application.eurder_project.service.validator.ItemValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemService {
    private final ItemRepository repository;
    private final ItemValidator validator;
    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    public ItemService(ItemRepository repository, ItemValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public List<Item> getAllItems() {return repository.getAll();}

    public Item createItem(PostItemRequest request) {
        logger.info("Verifying inputs.");
        validator.isValidRequest(request);
        logger.info("Input validated.");
        return repository.save(new Item(request.name(), request.description(), request.price(), request.stock()));
    }
}
