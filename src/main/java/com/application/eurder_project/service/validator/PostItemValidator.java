package com.application.eurder_project.service.validator;

import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.exception.RequestException;
import com.application.eurder_project.service.request.PostItemGroupRequest;
import com.application.eurder_project.service.request.PostItemRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PostItemValidator extends Validator{

    private final ItemRepository itemRepository;

    public PostItemValidator(ItemRepository itemRepository) {super();
        this.itemRepository = itemRepository;
    }

    public void isValidRequest(PostItemRequest request){
        if (isNotFilledIn(request.name())) throw RequestException.notFilledInField("item name");
        if (isNotFilledIn(request.description())) throw RequestException.notFilledInField("item description");
        if (isNotFilledIn(request.price()))throw RequestException.notFilledInField("item price");
        if (isNegative(request.price())) throw RequestException.negativeNumber("item price");
        if (isNotFilledIn(request.stock())) throw RequestException.notFilledInField("item stock amount");
        if (isNegative(request.stock())) throw RequestException.negativeNumber("item stock amount");

    }

    public void validItemGroups(List<PostItemGroupRequest> groups) {
        if (groups == null || groups.isEmpty()) throw RequestException.notFilledInField("order items");
        groups.forEach(g -> {
            if (g == null || g.itemId() == null) throw RequestException.notFilledInField("item id in order");
            existingItem(g.itemId());
            if (isNegative(g.amount())) throw RequestException.negativeNumber("item amount in order");
        });
    }

    public void existingItem(UUID itemId) {
        itemRepository.get(itemId).orElseThrow(() -> ExistenceException.notInDatabase("item"));
    }
}
