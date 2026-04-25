package dev.eventmanager.service;

import dev.eventcommon.model.EventDto;
import dev.eventcommon.model.RegistrationDto;
import dev.eventmanager.model.dto.event.EventCreateRequestDto;
import dev.eventmanager.model.dto.event.EventSearchRequestDto;
import dev.eventmanager.model.dto.event.EventUpdateRequestDto;

import java.util.List;


public interface EventService {

    EventDto createEvent(EventCreateRequestDto createDto);

    void deleteEvent(Long eventId);

    EventDto getEvent(Long eventId);

    EventDto updateEvent(Long eventId, EventUpdateRequestDto updateDto);

    List<EventDto> searchEvents(EventSearchRequestDto searchDto);

    List<EventDto> searchUserEvents();

    void registerForEvent(Long eventId);

    void cancelRegistration(Long eventId);

    List<RegistrationDto> searchRegistrations();

    List<RegistrationDto> getRegistrations();
}
