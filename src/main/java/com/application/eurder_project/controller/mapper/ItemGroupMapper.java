package com.application.eurder_project.controller.mapper;

import com.application.eurder_project.controller.dto.ItemGroupDto;
import com.application.eurder_project.domain.ItemGroup;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemGroupMapper {
    public ItemGroupDto toDto(ItemGroup group){
        return new ItemGroupDto(group.getItem().getId(),
                                group.getItem().getName(),
                                group.getAmount(),
                                group.getGroupPrice(),
                                group.getShippingDate());
    }

    public List<ItemGroupDto> toDtos(List<ItemGroup> groups){
        return groups.stream().map(this::toDto).toList();
    }
}
