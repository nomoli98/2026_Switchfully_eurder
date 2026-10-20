package com.application.eurder_project.service.validator;

import com.application.eurder_project.domain.user.Role;

public abstract class Validator {

    public boolean isNotFilledIn(String string) {
        return string == null || string.isBlank();
    }

    public boolean isNotFilledIn(int number) {
        return number == 0;
    }

    public boolean isNotFilledIn(double number) {
        return number == 0;
    }

    public boolean isNotFilledIn(Role role) {
        return role == null;
    }

    public boolean isNegative(double numberToVerify){
        return numberToVerify <= 0;
    }

    public boolean isNegative(int numberToVerify){
        return numberToVerify <= 0;
    }
}
