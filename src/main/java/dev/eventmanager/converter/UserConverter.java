package dev.eventmanager.converter;

import dev.eventmanager.entity.UserEntity;
import dev.eventmanager.model.Role;
import dev.eventmanager.model.dto.UserDto;
import dev.eventmanager.model.dto.UserRegistrationDto;
import org.springframework.stereotype.Component;


@Component
public class UserConverter {

    public static UserDto toDto(UserEntity entity) {
        return UserDto.builder()
                .id(entity.getId())
                .login(entity.getLogin())
                .age(entity.getAge())
                .role(Role.valueOf(entity.getRole()))
                .build();
    }

    public static UserEntity toEntity(UserDto dto) {
        return UserEntity.builder()
                .login(dto.getLogin())
                .passwordHash(dto.getPasswordHash())
                .age(dto.getAge())
                .role(dto.getRole().name())
                .build();
    }

    public static UserEntity toEntity(UserRegistrationDto dto) {
        return UserEntity.builder()
                .login(dto.getLogin())
                .passwordHash(dto.getPassword())
                .age(dto.getAge())
                .role(Role.USER.name())
                .build();
    }

}
