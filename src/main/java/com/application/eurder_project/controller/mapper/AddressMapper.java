package com.application.eurder_project.controller.mapper;

import com.application.eurder_project.controller.dto.AddressDto;
import com.application.eurder_project.domain.user.Address;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {
    public AddressDto toDto(Address address) {
        return new AddressDto(address.street(),
                              address.number(),
                              address.city(),
                              address.postalCode(),
                              address.country());
    }
}
