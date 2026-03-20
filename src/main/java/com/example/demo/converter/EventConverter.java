package com.example.demo.converter;

import com.example.demo.entity.EventEntity;
import com.example.demo.entity.RegistrationEntity;
import com.example.demo.exception.ServiceException;
import com.example.demo.model.EventStatus;
import com.example.demo.model.dto.event.EventCreateRequestDto;
import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.RegistrationDto;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;


public class EventConverter {

    public static EventDto toDto(EventEntity entity) {
        return EventDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .ownerId(entity.getOwnerId())
                .maxPlaces(entity.getMaxPlaces())
                .occupiedPlaces(entity.getOccupiedPlaces())
                .date(entity.getDate())
                .cost(entity.getCost())
                .duration(entity.getDuration())
                .locationId(entity.getLocationId())
                .status(EventStatus.valueOf(entity.getStatus()))
                .registrations(toDtoList(entity.getRegistrations()))
                .build();
    }

    public static EventEntity toEntity(EventCreateRequestDto dto) {
        return EventEntity.builder()
                .name(dto.getName())
                .maxPlaces(dto.getMaxPlaces())
                .occupiedPlaces(0)
                .date(dto.getDate())
                .cost(dto.getCost())
                .duration(dto.getDuration())
                .locationId(dto.getLocationId())
                .status(EventStatus.WAIT_START.name())
                .build();
    }

    private static List<RegistrationDto> toDtoList(List<RegistrationEntity> registrations) {
        if (registrations == null) {
            return new ArrayList<>();
        }
        return registrations.stream()
                .map(RegistrationConverter::toDto)
                .toList();
    }

    private static List<RegistrationEntity> toEntityList(List<RegistrationDto> registrations) {
        if (registrations == null) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "No registrations found");
        }
        return registrations.stream()
                .map(RegistrationConverter::toEntity)
                .toList();
    }

}
