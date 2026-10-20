package com.application.eurder_project.service.validator;

import com.application.eurder_project.domain.users.Address;
import com.application.eurder_project.domain.users.Role;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class CustomerValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

    public boolean isNotFilledIn(String string) {
        return string == null || string.isBlank();
    }

    public boolean isNotFilledIn(int number) {
        return number == 0;
    }

    public boolean isNotFilledIn(Role role) {
        return role == null;
    }

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
}