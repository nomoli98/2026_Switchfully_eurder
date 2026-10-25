package com.application.eurder_project.controller.mapper;

import com.application.eurder_project.controller.dto.ShippedTodayDto;
import com.application.eurder_project.service.response.ShippedToday;
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

    public ShippedTodayDto toDto(ShippedToday shippedToday) {
        return new ShippedTodayDto(itemGroupMapper.toDtos(shippedToday.items()),
                                   addressMapper.toDto(shippedToday.address()));
    }

    public List<ShippedTodayDto> toDtos(List<ShippedToday> shippedToday) {
        return shippedToday.stream().map(this::toDto).toList();
    }
}
