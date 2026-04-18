package dev.eventmanager.controller;

import dev.eventmanager.model.dto.LocationDto;
import dev.eventmanager.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<LocationDto>> getLocations() {
        log.info("GET request to get all locations");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(locationService.getLocations());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocationDto> createLocation(
            @RequestBody @Valid LocationDto locationDto) {
        log.info("POST request for location create: locationDto={}", locationDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(locationService.createLocation(locationDto));
    }

    @DeleteMapping("/{locationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteLocation(@PathVariable long locationId) {
        log.info("DELETE request to delete one location: locationId={}", locationId);
        locationService.deleteLocation(locationId);
    }

    @GetMapping("/{locationId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<LocationDto> getLocation(
            @PathVariable long locationId) {
        log.info("GET request to get one location: locationId={}", locationId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(locationService.getLocation(locationId));
    }

    @PutMapping("/{locationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocationDto> updateLocation(
            @PathVariable long locationId,
            @RequestBody @Valid LocationDto locationDto) {
        log.info("PUT request to update one location: locationId={}, locationDto={}",
                locationId, locationDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(locationService.updateLocation(locationId, locationDto));
    }

}
