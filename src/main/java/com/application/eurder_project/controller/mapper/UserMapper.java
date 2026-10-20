package com.application.eurder_project.controller.mapper;

import com.application.eurder_project.controller.dto.UserDto;
import com.application.eurder_project.domain.user.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public UserDto toDto(User customer) {
        return new UserDto(customer.getId(),
                           customer.getFirstname(),
                           customer.getLastname(),
                           customer.getEmail(),
                           customer.getPhoneNumber(),
                           customer.getAddress());
    }

    public List<UserDto> toDtos(List<User> customers){
        return customers.stream().map(this::toDto).toList();
    }
}
