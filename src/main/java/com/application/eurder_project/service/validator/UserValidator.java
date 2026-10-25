package com.application.eurder_project.service.validator;

import com.application.eurder_project.service.exception.RequestException;
import com.application.eurder_project.service.request.PostUserRequest;

import java.util.regex.Pattern;

import static com.application.eurder_project.service.validator.ValidationUtils.isNotFilledIn;

public final class UserValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
    /** Optional leading +, then 8 to 15 digits. */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?\\d{8,15}$");
    /** Spaces, dots and dashes are allowed as separators: "+32 470 12 34 56". */
    private static final Pattern PHONE_SEPARATORS = Pattern.compile("[\\s.\\-]");

    private UserValidator() {
    }

    public static void validateNewUser(PostUserRequest request) {
        if (isNotFilledIn(request.firstname())) throw RequestException.notFilledInField("user firstname");
        if (isNotFilledIn(request.lastname())) throw RequestException.notFilledInField("user lastname");
        if (isNotFilledIn(request.email())) throw RequestException.notFilledInField("user email");
        if (!isValidEmail(request.email())) throw RequestException.invalidField("user email");
        if (isNotFilledIn(request.phoneNumber())) throw RequestException.notFilledInField("user phone number");
        if (!isValidPhoneNumber(request.phoneNumber())) throw RequestException.invalidField("user phone number");
        if (!hasValidAddress(request)) throw RequestException.invalidField("user address");
        if (isNotFilledIn(request.role())) throw RequestException.notFilledInField("user role");
    }

    static boolean isValidEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }

    static boolean isValidPhoneNumber(String phoneNumber) {
        String withoutSeparators = PHONE_SEPARATORS.matcher(phoneNumber).replaceAll("");
        return PHONE_PATTERN.matcher(withoutSeparators).matches();
    }

    /** The city is optional, the other address fields are required. */
    private static boolean hasValidAddress(PostUserRequest request) {
        return !isNotFilledIn(request.street())
               && !isNotFilledIn(request.number())
               && !isNotFilledIn(request.postalCode())
               && !isNotFilledIn(request.country());
    }
}
