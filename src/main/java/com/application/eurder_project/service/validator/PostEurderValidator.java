package com.application.eurder_project.service.validator;

import com.application.eurder_project.service.exception.RequestException;
import com.application.eurder_project.service.request.PostEurderRequest;
import org.springframework.stereotype.Component;

@Component
public class PostEurderValidator extends Validator {

    private final PostUserValidator userValidator;
    private final PostItemValidator itemValidator;

    public PostEurderValidator(PostUserValidator userValidator, PostItemValidator itemValidator) {
        this.userValidator = userValidator;
        this.itemValidator = itemValidator;
    }

    public void isValidRequest(PostEurderRequest request) {
        if (request == null) throw RequestException.notFilledInField("order");
        itemValidator.validItemGroups(request.items());
    }

}
