package com.application.eurder_project.service.validator;

import com.application.eurder_project.domain.user.Role;

public abstract class Validator {

    public boolean isNotFilledIn(String string) {
        return string == null || string.isBlank();
    }

    public boolean isNotFilledIn(Integer number) {
        return number == null;
    }

    public boolean isNotFilledIn(Double number) {
        return number == null || number.isNaN();
    }

    public boolean isNotFilledIn(Role role) {
        return role == null;
    }

    public boolean isNegative(Double numberToVerify){
        if(numberToVerify == null) return false;
        return numberToVerify <= 0;
    }

    public boolean isNegative(Integer numberToVerify){
        if(numberToVerify == null) return false;
        return numberToVerify <= 0;
    }
}
