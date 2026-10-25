package com.application.eurder_project.controller.mapper;

import com.application.eurder_project.controller.dto.ItemDto;
import com.application.eurder_project.domain.Item;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemMapper {
    public ItemDto toDto(Item item) {
        return new ItemDto(item.getId(),
                           item.getName(),
                           item.getDescription(),
                           item.getPrice(),
                           item.getStock());
    }

    public List<ItemDto> toDtos(List<Item> itemList) {
        return itemList.stream().map(this::toDto).toList();
    }
}
