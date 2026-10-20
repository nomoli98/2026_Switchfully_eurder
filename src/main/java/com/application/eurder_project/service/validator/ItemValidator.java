package com.application.eurder_project.service.validator;

import com.application.eurder_project.service.exception.RequestException;
import com.application.eurder_project.service.request.PostItemRequest;
import org.springframework.stereotype.Component;

@Component
public class ItemValidator extends Validator{

    public boolean isNegative(double numberToVerify){
        return numberToVerify <= 0;
    }

    public boolean isNegative(int numberToVerify){
        return numberToVerify <= 0;
    }

    public void isValidRequest(PostItemRequest request){
        if (isNotFilledIn(request.name())) throw RequestException.notFilledOutField("item name");
        if (isNotFilledIn(request.description())) throw RequestException.notFilledOutField("item description");
        if (isNotFilledIn(request.price()))throw RequestException.notFilledOutField("item price");
        if (isNegative(request.price())) throw RequestException.negativeNumber("item price");
        if (isNotFilledIn(request.stock())) throw RequestException.notFilledOutField("item stock amount");
        if (isNegative(request.stock())) throw RequestException.negativeNumber("item stock amount");

    }
}
