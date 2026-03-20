package com.example.demo.converter;

import com.example.demo.entity.User;
import com.example.demo.model.Role;
import com.example.demo.model.dto.UserDto;


public class UserConverter {

    public static UserDto toDto(User entity) {
        return UserDto.builder()
                .id(entity.getId())
                .login(entity.getLogin())
                .age(entity.getAge())
                .role(Role.valueOf(entity.getRole()))
                .build();
    }

    public static User toEntity(UserDto dto) {
        return User.builder()
                .login(dto.getLogin())
                .age(dto.getAge())
                .role(dto.getRole().name())
                .build();
    }

}
