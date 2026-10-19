package com.application.eurder_project.service.validator;

import com.application.eurder_project.domain.Address;


import java.util.regex.Pattern;

public class CustomerValidator {

    public boolean isNotFilledIn(String string) {
        return string == null || string.isEmpty();
    }

    public boolean isNotFilledIn(int number) {
        return number == 0;
    }

    public boolean isValidEmail(String string) {
        Pattern pattern = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\\\.[A-Za-z0-9-]+)*(\\\\.[A-Za-z]{2,})$\n" + "\n");
        return pattern.matcher(string).matches();
    }

    public boolean isValidPhoneNumber(String string) {
        Pattern pattern = Pattern.compile("^\\d{10}$");
        return pattern.matcher(string).matches();
    }

    public boolean isValidAddress(Address address) {
        if (isNotFilledIn(address.street())) return false;
        if (isNotFilledIn(address.number())) return false;
        if (isNotFilledIn(address.country())) return false;
        if (isNotFilledIn(address.postalCode())) return false;
        return true;
    }


}
