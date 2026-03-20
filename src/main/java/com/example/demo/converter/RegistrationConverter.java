package com.example.demo.converter;

import com.example.demo.entity.Registration;
import com.example.demo.model.dto.RegistrationDto;

public class RegistrationConverter {

    public static Registration toEntity(RegistrationDto dto) {
        return Registration.builder()
                .userId(dto.getUserId())
                .registrationDate(dto.getRegistrationDate())
                .build();
    }

    public static RegistrationDto toDto(Registration entity) {
        return RegistrationDto.builder()
                .id(entity.getId())
                .eventId(entity.getEvent().getId())
                .userId(entity.getUserId())
                .registrationDate(entity.getRegistrationDate())
                .build();
    }
}
