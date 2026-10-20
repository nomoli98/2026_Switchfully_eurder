package com.application.eurder_project.service.validator;

import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.service.request.PutItemRequest;
import org.springframework.stereotype.Component;

@Component
public class PutItemValidator extends Validator{

    private final ItemRepository itemRepository;

    public PutItemValidator(ItemRepository itemRepository) {this.itemRepository = itemRepository;}

    public void isValidRequest(PutItemRequest request) {}
}
