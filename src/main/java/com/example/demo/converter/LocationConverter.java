package com.example.demo.converter;

import com.example.demo.entity.LocationEntity;
import com.example.demo.model.dto.LocationDto;


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
