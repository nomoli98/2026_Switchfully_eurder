package com.application.eurder_project.service.validator;

import com.application.eurder_project.domain.Item;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.exception.RequestException;
import com.application.eurder_project.service.request.PutItemRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PutItemValidator extends Validator{

    private final ItemRepository itemRepository;

    public PutItemValidator(ItemRepository itemRepository) {this.itemRepository = itemRepository;}

    public void isValidRequest(UUID itemID, PutItemRequest request) {
        Item existingItem = existingItem(itemID);
        if(isNotFilledIn(request.getName())) request.setName(existingItem.getName());
        if(isNotFilledIn(request.getDescription())) request.setDescription(existingItem.getDescription());
        if(isNegative(request.getPrice()))throw RequestException.negativeNumber("item price");
        if(isNegative(request.getStock()))throw RequestException.negativeNumber("item stock");
        if(isNotFilledIn(request.getPrice())) request.setPrice(existingItem.getPrice());
        if(isNotFilledIn(request.getStock())) request.setStock(existingItem.getStock());
    }

    public Item existingItem(UUID itemId) {
        return itemRepository.get(itemId).orElseThrow(() -> ExistenceException.NotInDatabase("item"));
    }
}
