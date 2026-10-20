package com.application.eurder_project.controller.mapper;

import com.application.eurder_project.controller.dto.UserDto;
import com.application.eurder_project.domain.user.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserMapper {
    public UserDto toDto(User user) {
        return new UserDto(user.getId(),
                           user.getFirstname(),
                           user.getLastname(),
                           user.getEmail(),
                           user.getPhoneNumber(),
                           user.getAddress());
    }

    public List<UserDto> toDtos(List<User> users){
        return users.stream().map(this::toDto).toList();
    }
}
