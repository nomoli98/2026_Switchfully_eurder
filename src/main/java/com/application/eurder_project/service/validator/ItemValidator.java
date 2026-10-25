package com.application.eurder_project.service.validator;

import com.application.eurder_project.service.exception.RequestException;
import com.application.eurder_project.service.request.PostItemRequest;
import com.application.eurder_project.service.request.PutItemRequest;

import static com.application.eurder_project.service.validator.ValidationUtils.*;

public final class ItemValidator {

    private ItemValidator() {
    }

    /**
     * A new item needs every field, and its price and stock must be greater than 0.
     */
    public static void validateNewItem(PostItemRequest request) {
        if (isNotFilledIn(request.name())) throw RequestException.notFilledInField("item name");
        if (isNotFilledIn(request.description()))
            throw RequestException.notFilledInField("item description");
        if (isNotFilledIn(request.price())) throw RequestException.notFilledInField("item price");
        if (isNotPositive(request.price())) throw RequestException.notPositiveNumber("item price");
        if (isNotFilledIn(request.stock()))
            throw RequestException.notFilledInField("item stock amount");
        if (isNotPositive(request.stock()))
            throw RequestException.notPositiveNumber("item stock amount");
    }

    /**
     * An update only checks the fields that are filled in. The stock may be set to 0.
     */
    public static void validateItemUpdate(PutItemRequest request) {
        if (isNotPositive(request.price())) throw RequestException.notPositiveNumber("item price");
        if (isNegative(request.stock())) throw RequestException.negativeNumber("item stock");
    }
}
