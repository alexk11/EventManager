package dev.eventmanager.converter;

import dev.eventmanager.entity.LocationEntity;
import dev.eventmanager.model.dto.LocationDto;


public class LocationConverter {

    public static LocationDto toDto(LocationEntity entity) {
        return LocationDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .address(entity.getAddress())
                .capacity(entity.getCapacity())
                .description(entity.getDescription())
                .build();
    }

    public static LocationEntity toEntity(LocationDto dto) {
        return LocationEntity.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .capacity(dto.getCapacity())
                .description(dto.getDescription())
                .build();
    }

}
