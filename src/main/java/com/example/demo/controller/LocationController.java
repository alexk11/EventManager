package com.example.demo.controller;

import com.example.demo.model.dto.LocationDto;
import com.example.demo.service.LocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/locations")
public class LocationController {

    private final LocationService locationService;

    @GetMapping
    //@PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<LocationDto>> getLocations() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(locationService.getLocations());
    }

    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocationDto> createLocation(
            @RequestBody @Valid LocationDto locationDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(locationService.createLocation(locationDto));
    }

    @DeleteMapping("/{locationId}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocationDto> deleteLocation(
            @PathVariable long locationId) {
        return ResponseEntity
                .status(204)
                .body(locationService.deleteLocation(locationId));
    }

    @GetMapping("/{locationId}")
    //@PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<LocationDto> getLocation(
            @PathVariable long locationId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(locationService.getLocation(locationId));
    }

    @PutMapping("/{locationId}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LocationDto> updateLocation(
            @PathVariable long locationId,
            @RequestBody @Valid LocationDto locationDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(locationService.updateLocation(locationId, locationDto));
    }

}
