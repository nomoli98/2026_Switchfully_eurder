package com.application.eurder_project.service.validator;

import com.application.eurder_project.domain.Eurder;
import com.application.eurder_project.domain.user.Role;
import com.application.eurder_project.service.exception.ExistenceException;

import java.util.List;
import java.util.UUID;

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

    public Eurder isInEurderList(List<Eurder> eurderList, UUID uuid){
        return eurderList.stream().filter(e -> e.getId() == uuid).findAny().orElseThrow(() -> ExistenceException.NotInDatabase("This eurder is not in the database"));
    }
}
