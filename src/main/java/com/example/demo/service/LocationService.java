package com.example.demo.service;

import com.example.demo.converter.LocationConverter;
import com.example.demo.entity.Location;
import com.example.demo.exception.ServiceException;
import com.example.demo.model.dto.LocationDto;
import com.example.demo.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    /**
     * Get all locations
     *
     * @return
     */
    public List<LocationDto> getLocations() {
        log.info("Getting all locations");
        return locationRepository.findAll().stream()
                .map(LocationConverter::toDto)
                .toList();
    }

    /**
     * Create new location
     *
     * @return
     */
    public LocationDto createLocation(LocationDto locationDto) {
        log.info("Creating new location with the name '{}'", locationDto.getName());
        Location locationEntity =
                locationRepository.save(LocationConverter.toEntity(locationDto));
        return LocationConverter.toDto(locationEntity);
    }

    /**
     * Delete existing location
     *
     * @return
     */
    public LocationDto deleteLocation(long locationId) {
        log.info("Deleting location with id = {}", locationId);
        return locationRepository.findById(locationId)
                .map(item -> {
                    locationRepository.delete(item);
                    return LocationConverter.toDto(item);
                })
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Location not found"));
    }

    /**
     * Get location by id
     *
     * @param locationId
     * @return
     */
    public LocationDto getLocation(long locationId) {
        log.info("Getting location by the id = {}", locationId);
        return locationRepository
                .findById(locationId)
                .map(LocationConverter::toDto)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Location not found"));
    }

    /**
     * Update location
     *
     * @param locationId
     * @param updateDto
     * @return
     */
    public LocationDto updateLocation(long locationId, LocationDto updateDto) {
        log.info("Updating location '{}'", locationId);

        Location location = locationRepository
                .findById(locationId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Location not found"));

        if (updateDto.getCapacity() < location.getCapacity()) {
            throw new ServiceException(
                    HttpStatus.BAD_REQUEST.value(),
                    "Location capacity cannot be decreased");
        }

        Location toUpdate = Location.builder()
            .id(locationId)
            .name(updateDto.getName())
            .address(updateDto.getAddress())
            .capacity(updateDto.getCapacity())
            .description(updateDto.getDescription())
            .build();
        return LocationConverter.toDto(locationRepository.save(toUpdate));
    }

}
