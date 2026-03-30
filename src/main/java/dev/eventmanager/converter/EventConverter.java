package dev.eventmanager.converter;

import dev.eventmanager.entity.EventEntity;
import dev.eventmanager.entity.RegistrationEntity;
import dev.eventmanager.model.EventStatus;
import dev.eventmanager.model.dto.event.EventCreateRequestDto;
import dev.eventmanager.model.dto.event.EventDto;
import dev.eventmanager.model.dto.RegistrationDto;

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
                .status(entity.getStatus().name())
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
                .status(EventStatus.WAIT_START)
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

}
