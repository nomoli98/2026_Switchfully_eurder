package com.application.eurder_project.service.validator;

import com.application.eurder_project.domain.Item;
import com.application.eurder_project.domain.ItemGroup;
import com.application.eurder_project.repository.ItemRepository;
import com.application.eurder_project.service.exception.ExistenceException;
import com.application.eurder_project.service.exception.RequestException;
import com.application.eurder_project.service.request.PostItemRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemValidator extends Validator{

    private final ItemRepository itemRepository;

    public ItemValidator(ItemRepository itemRepository) {super();
        this.itemRepository = itemRepository;
    }

    public boolean isNegative(double numberToVerify){
        return numberToVerify <= 0;
    }

    public boolean isNegative(int numberToVerify){
        return numberToVerify <= 0;
    }

    public void isValidRequest(PostItemRequest request){
        if (isNotFilledIn(request.name())) throw RequestException.notFilledInField("item name");
        if (isNotFilledIn(request.description())) throw RequestException.notFilledInField("item description");
        if (isNotFilledIn(request.price()))throw RequestException.notFilledInField("item price");
        if (isNegative(request.price())) throw RequestException.negativeNumber("item price");
        if (isNotFilledIn(request.stock())) throw RequestException.notFilledInField("item stock amount");
        if (isNegative(request.stock())) throw RequestException.negativeNumber("item stock amount");

    }

    public void validItemGroup(List<ItemGroup> items) {
        items.forEach(i -> {
            existingItem(i.getItem());
            if(isNotFilledIn(i.getAmount())) throw RequestException.notFilledInField("item " + i.getItem().getName() + "amount in order");
        });
    }

    public void existingItem(Item item){
        itemRepository.get(item.getId()).orElseThrow(() -> ExistenceException.NotInDatabase("item"));
    }
}
