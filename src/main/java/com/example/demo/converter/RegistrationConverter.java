package com.example.demo.converter;

import com.example.demo.entity.RegistrationEntity;
import com.example.demo.model.dto.RegistrationDto;


public class RegistrationConverter {

    public static RegistrationEntity toEntity(RegistrationDto dto) {
        return RegistrationEntity.builder()
                .userId(dto.getUserId())
                .registrationDate(dto.getRegistrationDate())
                .build();
    }

    public static RegistrationDto toDto(RegistrationEntity entity) {
        return RegistrationDto.builder()
                .id(entity.getId())
                .eventId(entity.getEvent().getId())
                .userId(entity.getUserId())
                .registrationDate(entity.getRegistrationDate())
                .build();
    }
}
