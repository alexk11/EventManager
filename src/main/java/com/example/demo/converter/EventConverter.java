package com.example.demo.converter;

import com.example.demo.entity.Event;
import com.example.demo.entity.Registration;
import com.example.demo.exception.ServiceException;
import com.example.demo.model.EventStatus;
import com.example.demo.model.dto.EventCreateRequestDto;
import com.example.demo.model.dto.EventDto;
import com.example.demo.model.dto.RegistrationDto;
import org.springframework.http.HttpStatus;
import java.util.List;


public class EventConverter {

    public static EventDto toDto(Event entity) {
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

    public static Event toEntity(EventDto dto) {
        return Event.builder()
                .name(dto.getName())
                .ownerId(dto.getOwnerId())
                .maxPlaces(dto.getMaxPlaces())
                .occupiedPlaces(dto.getOccupiedPlaces())
                .date(dto.getDate())
                .cost(dto.getCost())
                .duration(dto.getDuration())
                .locationId(dto.getLocationId())
                .status(dto.getStatus().name())
                .registrations(toEntityList(dto.getRegistrations()))
                .build();
    }

    public static Event toEntity(EventCreateRequestDto dto) {
        return Event.builder()
                .name(dto.getName())
                .ownerId(dto.getOwnerId())
                .maxPlaces(dto.getMaxPlaces())
                .occupiedPlaces(0)
                .date(dto.getDate())
                .cost(dto.getCost())
                .duration(dto.getDuration())
                .locationId(dto.getLocationId())
                .status(EventStatus.WAIT_START.name())
                .build();
    }

    private static List<RegistrationDto> toDtoList(List<Registration> registrations) {
        if (registrations == null) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "No registrations found");
        }
        return registrations.stream()
                .map(RegistrationConverter::toDto)
                .toList();
    }

    private static List<Registration> toEntityList(List<RegistrationDto> registrations) {
        if (registrations == null) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "No registrations found");
        }
        return registrations.stream()
                .map(RegistrationConverter::toEntity)
                .toList();
    }

}
