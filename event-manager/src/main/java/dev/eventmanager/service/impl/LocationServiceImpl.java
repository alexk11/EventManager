package dev.eventmanager.service.impl;

import dev.eventmanager.converter.LocationConverter;
import dev.eventmanager.entity.LocationEntity;
import dev.eventcommon.exception.ServiceException;
import dev.eventmanager.model.dto.LocationDto;
import dev.eventmanager.repository.LocationRepository;
import dev.eventmanager.service.LocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    private static final String CACHE_KEY_PREFIX = "location:";

    /**
     * Get all locations
     *
     * @return
     */
    @Override
    @Cacheable(cacheNames = CACHE_KEY_PREFIX, key = "'all'")
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
    @Override
    @Caching(evict = {
        @CacheEvict(
                cacheNames = CACHE_KEY_PREFIX,
                key = "'all'"
        ),
        @CacheEvict(
                cacheNames = CACHE_KEY_PREFIX,
                key = "'id:' + #result.id()",
                condition = "#result != null"
        )
    })
    public LocationDto createLocation(LocationDto locationDto) {
        log.info("Creating new location with the name '{}'", locationDto.getName());
        LocationEntity locationEntity =
                locationRepository.save(LocationConverter.toEntity(locationDto));
        return LocationConverter.toDto(locationEntity);
    }

    /**
     * Delete existing location
     *
     * @return
     */
    @Override
    @Caching(evict = {
            @CacheEvict(
                    cacheNames = CACHE_KEY_PREFIX,
                    key = "'all'"
            ),
            @CacheEvict(
                    cacheNames = CACHE_KEY_PREFIX,
                    key = "'id:' + #locationId",
                    condition = "#result != null"
            )
    })
    public LocationDto deleteLocation(Long locationId) {
        log.info("Deleting location with id = {}", locationId);
        return locationRepository.findById(locationId)
                .map(item -> {
                    locationRepository.delete(item);
                    return LocationConverter.toDto(item);
                })
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(),
                        "Location with id = '" + locationId + "' not found"));
    }

    /**
     * Get location by id
     *
     * @param locationId
     * @return
     */
    @Override
    @Cacheable(
            cacheNames = CACHE_KEY_PREFIX,
            key = "'id:' + #locationId"
    )
    public LocationDto getLocation(Long locationId) {
        log.info("Getting location by the id = {}", locationId);
        return locationRepository
                .findById(locationId)
                .map(LocationConverter::toDto)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(),
                        "Location with id '" + locationId + "' not found"));
    }

    /**
     * Update location
     *
     * @param locationId
     * @param updateDto
     * @return
     */
    @Override
    @Caching(evict = {
            @CacheEvict(
                    cacheNames = CACHE_KEY_PREFIX,
                    key = "'all'"
            ),
            @CacheEvict(
                    cacheNames = CACHE_KEY_PREFIX,
                    key = "'id:' + #locationId",
                    condition = "#result != null"
            )
    })
    public LocationDto updateLocation(Long locationId, LocationDto updateDto) {
        log.info("Updating location '{}'", locationId);

        LocationEntity location = locationRepository
                .findById(locationId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(),
                        "Location not found"));

        if (updateDto.getCapacity() < location.getCapacity()) {
            throw new ServiceException(
                    HttpStatus.BAD_REQUEST.value(),
                    "Location capacity cannot be decreased");
        }

        LocationEntity toUpdate = LocationEntity.builder()
            .id(locationId)
            .name(updateDto.getName())
            .address(updateDto.getAddress())
            .capacity(updateDto.getCapacity())
            .description(updateDto.getDescription())
            .build();
        return LocationConverter.toDto(locationRepository.save(toUpdate));
    }

}
