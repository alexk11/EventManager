package com.example.demo.controller;

import com.example.demo.model.dto.EventCreateRequestDto;
import com.example.demo.model.dto.EventDto;
import com.example.demo.model.dto.EventSearchRequestDto;
import com.example.demo.model.dto.EventUpdateRequestDto;
import com.example.demo.model.dto.RegistrationDto;
import com.example.demo.service.EventService;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @PostMapping
    //@PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<EventDto> createEvent(
            @RequestBody @Valid EventCreateRequestDto createDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.createEvent(createDto));
    }

    @DeleteMapping("/{eventId}")
    //@PreAuthorize("hasAnyRole('ADMIN','USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable long eventId) {
        eventService.deleteEvent(eventId);
    }

    @GetMapping("/{eventId}")
    //@PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<EventDto> getEvent(
            @PathVariable long eventId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.getEvent(eventId));
    }

    @PutMapping("/{eventId}")
    //@PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<EventDto> updateEvent(
            @PathVariable long eventId,
            @RequestBody @Valid EventUpdateRequestDto updateDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.updateEvent(eventId, updateDto));
    }

    @PostMapping("/search")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EventDto>> searchEvents(
            @RequestBody @Valid EventSearchRequestDto searchDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.searchEvents(searchDto));
    }

    @GetMapping("/my/{userId}")
    //@PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<EventDto>> searchUserEvents(
            @PathVariable long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.searchUserEvents(userId));
    }

    @PostMapping("/registrations/{eventId}/{userId}")
    //@PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public void registerForEvent(
            @PathVariable long eventId,
            @PathVariable long userId) {
        eventService.registerForEvent(eventId, userId);
    }

    @GetMapping("/registrations")
    public ResponseEntity<List<RegistrationDto>> getRegistrations() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.getRegistrations());
    }

    @DeleteMapping("/registrations/cancel/{eventId}/{userId}")
    //@PreAuthorize("hasAnyRole('ADMIN','USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelRegistration(
            @PathVariable long eventId,
            @PathVariable long userId) {
        eventService.cancelRegistration(eventId, userId);
    }

    @GetMapping("/registrations/my/{userId}")
    //@PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<RegistrationDto>> searchRegistrations(
            @PathVariable long userId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.searchRegistrations(userId));
    }

}
