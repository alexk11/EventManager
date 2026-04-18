package dev.eventmanager.controller;

import dev.eventcommon.model.EventDto;
import dev.eventcommon.model.RegistrationDto;
import dev.eventmanager.model.dto.event.EventCreateRequestDto;
import dev.eventmanager.model.dto.event.EventSearchRequestDto;
import dev.eventmanager.model.dto.event.EventUpdateRequestDto;
import dev.eventmanager.service.EventService;
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
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<EventDto> createEvent(
            @RequestBody @Valid EventCreateRequestDto createDto) {
        log.info("POST request to create an event: createDto={}",
                createDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.createEvent(createDto));
    }


    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEvent(@PathVariable long eventId) {
        log.info("DELETE request to delete an event: eventId={}",
                eventId);
        eventService.deleteEvent(eventId);
    }


    @GetMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<EventDto> getEvent(
            @PathVariable long eventId) {
        log.info("GET request to get an event: eventId={}",
                eventId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.getEvent(eventId));
    }


    @PutMapping("/{eventId}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<EventDto> updateEvent(
            @PathVariable long eventId,
            @RequestBody @Valid EventUpdateRequestDto updateDto) {
        log.info("PUT request to update event: eventId={}, updateDto={}",
                eventId, updateDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.updateEvent(eventId, updateDto));
    }


    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<EventDto>> searchEvents(
            @RequestBody @Valid EventSearchRequestDto searchDto) {
        log.info("POST request to search for events: searchDto={}",
                searchDto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.searchEvents(searchDto));
    }


    @GetMapping("/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<EventDto>> searchUserEvents() {
        log.info("GET request to find all events of current user");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.searchUserEvents());
    }


    @PostMapping("/registrations/{eventId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public void registerForEvent(@PathVariable long eventId) {
        log.info("POST request to register for event: eventId={}",
                eventId);
        eventService.registerForEvent(eventId);
    }


    @GetMapping("/registrations")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<RegistrationDto>> getRegistrations() {
        log.info("GET request to search all registrations");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.getRegistrations());
    }


    @DeleteMapping("/registrations/cancel/{eventId}")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelRegistration(@PathVariable long eventId) {
        log.info("DELETE request to cancel user's registration for the event: eventId={}",
                eventId);
        eventService.cancelRegistration(eventId);
    }


    @GetMapping("/registrations/my")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<RegistrationDto>> searchRegistrations() {
        log.info("GET request to search registration of current user");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.searchRegistrations());
    }

}
