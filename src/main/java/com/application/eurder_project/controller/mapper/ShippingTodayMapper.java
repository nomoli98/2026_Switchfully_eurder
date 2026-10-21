package com.application.eurder_project.controller.mapper;

import com.application.eurder_project.controller.dto.ShippedTodayDto;
import com.application.eurder_project.domain.Eurder;
import com.application.eurder_project.domain.ItemGroup;
import com.application.eurder_project.domain.user.Address;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ShippingTodayMapper {

    private final ItemGroupMapper itemGroupMapper;
    private final AddressMapper addressMapper;

    public ShippingTodayMapper(ItemGroupMapper itemGroupMapper, AddressMapper addressMapper) {
        this.itemGroupMapper = itemGroupMapper;
        this.addressMapper = addressMapper;
    }

    public ShippedTodayDto toDto(Eurder eurder) {
        return new ShippedTodayDto(itemGroupMapper.toDtos(eurder.getItems()),
                                   addressMapper.toDto(eurder.getUser().getAddress())

        );
    }


    public ShippedTodayDto toDto(List<ItemGroup> items, Address address) {
        return new ShippedTodayDto(itemGroupMapper.toDtos(items),
                                   addressMapper.toDto(address)

        );
    }
}
