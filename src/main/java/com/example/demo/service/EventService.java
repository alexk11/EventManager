package com.example.demo.service;

import com.example.demo.converter.EventConverter;
import com.example.demo.converter.RegistrationConverter;
import com.example.demo.entity.Event;
import com.example.demo.entity.Registration;
import com.example.demo.exception.ServiceException;
import com.example.demo.model.dto.EventCreateRequestDto;
import com.example.demo.model.dto.EventDto;
import com.example.demo.model.dto.EventSearchRequestDto;
import com.example.demo.model.dto.EventUpdateRequestDto;
import com.example.demo.model.dto.RegistrationDto;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.RegistrationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final RegistrationRepository registrationRepository;

    /**
     * Create new event
     * @param createDto
     * @return
     */
    public EventDto createEvent(EventCreateRequestDto createDto) {
        log.info("Creating event '{}'", createDto.getName());
        Event saved = eventRepository.save(EventConverter.toEntity(createDto));
        return EventConverter.toDto(saved);
    }

    /**
     * Delete existing event and the registrations
     * @param eventId
     */
    public void deleteEvent(long eventId) {
        log.info("Deleting event with id = '{}'", eventId);
        eventRepository.findById(eventId)
            .map(item -> {
                eventRepository.delete(item);
                return eventId;
            })
            .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Мероприятие не найдено"));
    }

    /**
     * Get event info
     * @param eventId
     * @return
     */
    public EventDto getEvent(long eventId) {
        log.info("Getting event with id = '{}'", eventId);
        return eventRepository.findById(eventId)
                .map(EventConverter::toDto)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Мероприятие не найдено"));
    }

    /**
     * Update event info
     * @param eventId
     * @param updateDto
     * @return
     */
    public EventDto updateEvent(long eventId, EventUpdateRequestDto updateDto) {
        log.info("Updating event with id = '{}'", eventId);
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Мероприятие не найдено"));

        if (updateDto.getMaxPlaces() < event.getOccupiedPlaces()) {
            throw new ServiceException(
                    HttpStatus.BAD_REQUEST.value(),
                    "Количество доступных мест не должно быть меньше количества регистраций");
        }

        Event toUpdate = Event.builder()
                .id(eventId)
                .name(updateDto.getName())
                .cost(updateDto.getCost())
                .date(updateDto.getDate())
                .duration(updateDto.getDuration())
                .locationId(updateDto.getLocationId())
                .maxPlaces(updateDto.getMaxPlaces())
                .build();
        return EventConverter.toDto(eventRepository.save(toUpdate));
    }

    /**
     * Find events according to the search criteria
     * @param searchDto
     * @return
     */
    public List<EventDto> searchEvents(EventSearchRequestDto searchDto) {
        log.info("Searching for events according to filter criteria");
        List<EventDto> result = new ArrayList<>();
        eventRepository.findEventsByFilterParams(
                searchDto.getDateStartAfter(),
                searchDto.getDateStartBefore(),
                searchDto.getDurationMin(),
                searchDto.getDurationMax(),
                searchDto.getPlacesMin(),
                searchDto.getPlacesMax(),
                searchDto.getLocationId(),
                searchDto.getEventStatus().name(),
                searchDto.getName(),
                searchDto.getCostMin(),
                searchDto.getCostMax()).forEach(
                        item -> result.add(EventConverter.toDto(item)));
        return result;
    }

    /**
     * Find events by owner id
     * @param ownerId
     * @return
     */
    public List<EventDto> searchUserEvents(long ownerId) {
        log.info("Searching for user '{}' events", ownerId);
        List<EventDto> result = new ArrayList<>();
        eventRepository.findByOwnerId(ownerId).forEach(
                item -> result.add(EventConverter.toDto(item)));
        return result;
    }

    /**
     * Create new event registration
     * @param eventId
     * @param userId
     */
    @Transactional
    public void registerForEvent(long eventId, long userId) {
        log.info("Registering user '{}' for event '{}'", userId, eventId);
        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Event not found"));

        for (Registration re : event.getRegistrations()) {
            if (re.getUserId().equals(userId)) {
                throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "User already registered");
            }
        }

        if (event.getMaxPlaces().equals(event.getOccupiedPlaces())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "There is no seat available");
        }

        Registration registrationEntity = Registration.builder()
                .userId(userId)
                .registrationDate(LocalDateTime.now())
                .event(event)
                .build();
        registrationRepository.save(registrationEntity);

        event.setOccupiedPlaces(event.getOccupiedPlaces() + 1);
        eventRepository.save(event);
    }

    /**
     * Cancel user registration for the event
     * @param eventId name of the event
     * @param userId the user id
     */
    @Transactional
    public void cancelRegistration(long eventId, long userId) {
        log.info("Cancelling registration of user '{}' for event '{}'", userId, eventId);

        Event event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Event not found"));

        if (event.getDate().isBefore(LocalDateTime.now())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Мероприятие уже началось или закончилось");
        }

        registrationRepository
                .findByUserIdAndEventId(userId, eventId)
                .map(r -> {
                    registrationRepository.delete(r);
                    return r;
                })
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Регистрация не найдена"));

        event.setOccupiedPlaces(event.getOccupiedPlaces() - 1);
        eventRepository.save(event);
    }

    /**
     * Find registrations according to filter criteria
     * @param userId
     * @return
     */
    public List<RegistrationDto> searchRegistrations(long userId) {
        log.info("Obtain registrations of user '{}'", userId);
        List<RegistrationDto> result = new ArrayList<>();
        registrationRepository
                .findByUserId(userId).forEach(
                r -> result.add(RegistrationConverter.toDto(r)));
        return result;
    }

    /**
     * Get all registrations
     * @return
     */
    public List<RegistrationDto> getRegistrations() {
        log.info("Getting all registrations");
        List<RegistrationDto> result = new ArrayList<>();
        registrationRepository
                .findAll().forEach(
                        r -> result.add(RegistrationConverter.toDto(r)));
        return result;
    }

}
