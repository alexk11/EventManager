package dev.eventmanager.converter;

import dev.eventcommon.model.RegistrationDto;
import dev.eventmanager.entity.RegistrationEntity;


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
