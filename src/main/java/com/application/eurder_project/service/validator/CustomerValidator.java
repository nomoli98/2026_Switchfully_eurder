package com.application.eurder_project.service.validator;

import com.application.eurder_project.domain.Address;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.regex.Pattern;

public class CustomerValidator {
    private EmailValidator emailValidator;

    public CustomerValidator(EmailValidator emailValidator) {
        this.emailValidator = emailValidator;
    }

    public boolean isNotFilledIn(String string){
        return string == null && string.isEmpty();
    }

    public boolean isNotFilledIn(int number){
        return number==0;
    }

    public boolean isValidEmail(String string){
        return this.emailValidator.isValid(string);
    }

    public boolean isValidPhoneNumber(String string){
        Pattern pattern = Pattern.compile("^\\d{10}$");
        return pattern.matcher(string).matches();
    }

    public boolean isValidAddress (Address address){
        if (isNotFilledIn(address.street()))return false;
        if (isNotFilledIn(address.number()))return false;
        if (isNotFilledIn(address.country()))return false;
        if (isNotFilledIn(address.postalCode()))return false;
        return true;
    }


}
