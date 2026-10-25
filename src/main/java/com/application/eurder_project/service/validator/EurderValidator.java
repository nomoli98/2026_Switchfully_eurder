package com.application.eurder_project.service.validator;

import com.application.eurder_project.service.exception.RequestException;
import com.application.eurder_project.service.request.PostEurderRequest;
import com.application.eurder_project.service.request.PostItemGroupRequest;

import java.util.List;

import static com.application.eurder_project.service.validator.ValidationUtils.isNotPositive;

public final class EurderValidator {

    private EurderValidator() {
    }

    /** Checks the shape of the request only. Whether the items exist is checked by the service. */
    public static void validateNewEurder(PostEurderRequest request) {
        if (request == null) throw RequestException.notFilledInField("order");
        validateItemGroups(request.items());
    }

    private static void validateItemGroups(List<PostItemGroupRequest> groups) {
        if (groups == null || groups.isEmpty()) throw RequestException.notFilledInField("order items");
        groups.forEach(group -> {
            if (group == null || group.itemId() == null) throw RequestException.notFilledInField("item id in order");
            if (isNotPositive(group.amount())) throw RequestException.notPositiveNumber("item amount in order");
        });
    }
}
