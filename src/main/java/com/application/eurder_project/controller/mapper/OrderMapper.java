package com.application.eurder_project.controller.mapper;

import com.application.eurder_project.controller.dto.OrderDto;
import com.application.eurder_project.domain.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    private final ItemGroupMapper itemGroupMapper;

    public OrderMapper(ItemGroupMapper itemGroupMapper) {this.itemGroupMapper = itemGroupMapper;}

    public OrderDto toDto(Order order){
        return new OrderDto(order.getId(),
                            order.getTotalPrice(),
                            itemGroupMapper.toDtos(order.getItems()),
                            order.getUser().getId()
        );
    }

    public List<OrderDto> toDtos(List<Order> orders){
        return orders.stream().map(this::toDto).toList();
    }
}
