package com.application.eurder_project.service.validator;

import com.application.eurder_project.domain.user.Address;
import com.application.eurder_project.domain.user.Role;
import com.application.eurder_project.service.exception.RequestException;
import com.application.eurder_project.service.request.PostUserRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class CustomerValidator extends Validator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

    public boolean isValidEmail(String string) {
        return EMAIL_PATTERN.matcher(string).matches();
    }

    public boolean isValidPhoneNumber(String string) {
        return PHONE_PATTERN.matcher(string).matches();
    }

    public boolean isValidAddress(Address address) {
        if (address == null) return false;
        if (isNotFilledIn(address.street())) return false;
        if (isNotFilledIn(address.number())) return false;
        if (isNotFilledIn(address.country())) return false;
        if (isNotFilledIn(address.postalCode())) return false;
        return true;
    }

    public boolean isValidRole(Role role) {
        List<Role> roles = Arrays.asList(Role.values());
        return roles.contains(role);
    }
    
    public void isValidRequest(PostUserRequest request, Address address){
        if (isNotFilledIn(request.firstname())) throw RequestException.notFilledOutField("user firstname");
        if (isNotFilledIn(request.lastname())) throw RequestException.notFilledOutField("user lastname");
        if (isNotFilledIn(request.email())) throw RequestException.notFilledOutField("user email");
        if (!isValidEmail(request.email())) throw RequestException.invalidField("user email");
        if (isNotFilledIn(request.phoneNumber())) throw RequestException.notFilledOutField("user phone number");
        if (!isValidPhoneNumber(request.phoneNumber())) throw RequestException.invalidField("user phone number");
        if (!isValidAddress(address)) throw RequestException.invalidField("user address");
        if (isNotFilledIn(request.role())) throw RequestException.notFilledOutField("user role");
        if (!isValidRole(request.role())) throw RequestException.invalidField("user role");
    }
}