package com.example.demo.service;

import com.example.demo.converter.EventConverter;
import com.example.demo.converter.RegistrationConverter;
import com.example.demo.entity.EventEntity;
import com.example.demo.entity.RegistrationEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.exception.ServiceException;
import com.example.demo.model.EventStatus;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.dto.event.EventCreateRequestDto;
import com.example.demo.model.dto.event.EventDto;
import com.example.demo.model.dto.event.EventSearchRequestDto;
import com.example.demo.model.dto.event.EventUpdateRequestDto;
import com.example.demo.model.dto.RegistrationDto;
import com.example.demo.repository.EventRepository;
import com.example.demo.repository.LocationRepository;
import com.example.demo.repository.RegistrationRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RegistrationRepository registrationRepository;

    /**
     * Create new event
     * @param createDto
     * @return
     */
    public EventDto createEvent(EventCreateRequestDto createDto) {
        log.info("Creating event '{}'", createDto.getName());
        return userRepository.findByLogin(getLoginFromJwtToken()).map(user -> {
            EventEntity toCreate = EventConverter.toEntity(createDto);
            toCreate.setOwnerId(user.getId());
            return EventConverter.toDto(eventRepository.save(toCreate));
        }).orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "User not found"));
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
            .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Event not found"));
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
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Event not found"));
    }

    /**
     * Update event info
     * @param eventId
     * @param updateDto
     * @return
     */
    public EventDto updateEvent(long eventId, EventUpdateRequestDto updateDto) {
        log.info("Updating event with id = '{}'", eventId);
        EventEntity event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Event not found"));

        if (updateDto.getMaxPlaces() < event.getOccupiedPlaces()) {
            throw new ServiceException(
                    HttpStatus.BAD_REQUEST.value(),
                    "The number of available seats must not be less than the number of registrations");
        }

        if (!locationRepository.existsById((long) updateDto.getLocationId())) {
            throw new ServiceException(HttpStatus.NOT_FOUND.value(), "Location not found");
        }

        EventEntity toUpdate = EventEntity.builder()
                .id(eventId)
                .name(updateDto.getName())
                .ownerId(event.getOwnerId())
                .cost(updateDto.getCost())
                .date(updateDto.getDate())
                .duration(updateDto.getDuration())
                .locationId(updateDto.getLocationId())
                .occupiedPlaces(event.getOccupiedPlaces())
                .maxPlaces(updateDto.getMaxPlaces())
                .status(event.getStatus())
                .registrations(event.getRegistrations())
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
        return eventRepository.findEventsByFilterParams(
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
                searchDto.getCostMax())
                    .stream().map(EventConverter::toDto).toList();
    }

    /**
     * Find events of current user
     * @return
     */
    public List<EventDto> searchUserEvents() {
        log.info("Searching user's events");
        Optional<UserEntity> userEntity = userRepository.findByLogin(getLoginFromJwtToken());
        if (userEntity.isEmpty()) {
            throw new ServiceException(HttpStatus.NOT_FOUND.value(), "User not found");
        }
        return eventRepository
                .findByOwnerId(userEntity.get().getId()).stream()
                .map(EventConverter::toDto)
                .toList();
    }

    /**
     * Create new event registration
     * @param eventId
     */
    @Transactional
    public void registerForEvent(long eventId) {
        log.info("Registering user for event '{}'", eventId);
        EventEntity event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Event not found"));

        UserEntity userEntity = userRepository
                .findByLogin(getLoginFromJwtToken())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "User not found"));

        if (event.getDate().isBefore(LocalDateTime.now())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Event has already started or ended");
        }

        if (event.getStatus().equals(EventStatus.CANCELLED.name())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Event has been canceled");
        }

        for (RegistrationEntity re : event.getRegistrations()) {
            if (re.getUserId().equals(userEntity.getId())) {
                throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "User already registered");
            }
        }

        if (event.getMaxPlaces().equals(event.getOccupiedPlaces())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "There is no seat available");
        }

        RegistrationEntity registrationEntity = RegistrationEntity.builder()
                .userId(userEntity.getId())
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
     */
    @Transactional
    public void cancelRegistration(long eventId) {
        log.info("Cancelling registration for event '{}'", eventId);

        EventEntity event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Event not found"));

        if (event.getDate().isBefore(LocalDateTime.now())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Event has already started or ended");
        }

        UserEntity dbUser = userRepository
                .findByLogin(getLoginFromJwtToken())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "User not found"));

        registrationRepository
                .findByUserIdAndEventId(dbUser.getId(), eventId)
                .map(r -> {
                    registrationRepository.delete(r);
                    return r;
                })
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "User's registration not found"));

        event.setOccupiedPlaces(event.getOccupiedPlaces() - 1);
        eventRepository.save(event);
    }

    /**
     * Find registrations of current user
     * @return
     */
    public List<RegistrationDto> searchRegistrations() {
        log.info("Obtaining user's registrations");
        UserEntity dbUser = userRepository
                .findByLogin(getLoginFromJwtToken())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "User not found"));
        return registrationRepository
                .findByUserId(dbUser.getId()).stream()
                .map(RegistrationConverter::toDto)
                .toList();
    }

    /**
     * Get all registrations of all users
     * @return
     */
    public List<RegistrationDto> getRegistrations() {
        log.info("Getting all registrations");
        return registrationRepository.findAll().stream()
                .map(RegistrationConverter::toDto)
                .toList();
    }

    private String getLoginFromJwtToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDto userDto) {
            return userDto.getLogin();
        }
        return null;
    }

}
