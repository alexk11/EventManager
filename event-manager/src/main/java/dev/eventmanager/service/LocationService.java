package dev.eventmanager.service;

import dev.eventmanager.model.dto.LocationDto;

import java.util.List;

public interface LocationService {

    List<LocationDto> getLocations();

    LocationDto createLocation(LocationDto locationDto);

    LocationDto deleteLocation(Long locationId);

    LocationDto getLocation(Long locationId);

    LocationDto updateLocation(Long locationId, LocationDto updateDto);

}
