package com.application.eurder_project.service.validator;

import com.application.eurder_project.service.exception.RequestException;
import com.application.eurder_project.service.request.PostOrderRequest;
import org.springframework.stereotype.Component;

@Component
public class PostOrderValidator extends Validator {

    private final PostUserValidator userValidator;
    private final PostItemValidator itemValidator;

    public PostOrderValidator(PostUserValidator userValidator, PostItemValidator itemValidator) {
        this.userValidator = userValidator;
        this.itemValidator = itemValidator;
    }

    public void isValidRequest(PostOrderRequest request) {
        if (request == null) throw RequestException.notFilledInField("order");
        itemValidator.validItemGroups(request.items());
    }
}
