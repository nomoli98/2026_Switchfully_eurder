package com.application.eurder_project.service.validator;

import com.application.eurder_project.service.request.PostOrderRequest;
import org.springframework.stereotype.Component;

@Component
public class OrderValidator extends Validator {

    private final UserValidator userValidator;
    private final ItemValidator itemValidator;

    public OrderValidator(UserValidator userValidator, ItemValidator itemValidator) {
        this.userValidator = userValidator;
        this.itemValidator = itemValidator;
    }

    public void isValidRequest(PostOrderRequest request) {
        userValidator.existingUser(request.user());
        itemValidator.validItemGroup(request.items());
    }
}
