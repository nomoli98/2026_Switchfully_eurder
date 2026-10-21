package com.application.eurder_project.controller.mapper;

import com.application.eurder_project.controller.dto.EurderDto;
import com.application.eurder_project.domain.Eurder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EurderMapper {

    private final ItemGroupMapper itemGroupMapper;

    public EurderMapper(ItemGroupMapper itemGroupMapper) {this.itemGroupMapper = itemGroupMapper;}

    public EurderDto toDto(Eurder eurder){
        return new EurderDto(eurder.getId(),
                             eurder.getTotalPrice(),
                             itemGroupMapper.toDtos(eurder.getItems()),
                             eurder.getUser().getId()
        );
    }

    public List<EurderDto> toDtos(List<Eurder> eurders){
        return eurders.stream().map(this::toDto).toList();
    }
}
