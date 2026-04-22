package dev.eventmanager.service;

import dev.eventcommon.kafka.ChangeItem;
import dev.eventcommon.kafka.EventChangeMessage;
import dev.eventcommon.kafka.EventType;
import dev.eventcommon.model.EventDto;
import dev.eventcommon.model.RegistrationDto;
import dev.eventmanager.async.KafkaMessageSender;
import dev.eventmanager.converter.EventConverter;
import dev.eventmanager.converter.RegistrationConverter;
import dev.eventmanager.converter.UserConverter;
import dev.eventmanager.entity.EventEntity;
import dev.eventmanager.entity.LocationEntity;
import dev.eventmanager.entity.RegistrationEntity;
import dev.eventmanager.entity.UserEntity;
import dev.eventcommon.exception.ServiceException;
import dev.eventmanager.model.EventStatus;
import dev.eventmanager.model.dto.UserDto;
import dev.eventmanager.model.dto.event.EventCreateRequestDto;
import dev.eventmanager.model.dto.event.EventSearchRequestDto;
import dev.eventmanager.model.dto.event.EventUpdateRequestDto;
import dev.eventmanager.repository.EventRepository;
import dev.eventmanager.repository.LocationRepository;
import dev.eventmanager.repository.RegistrationRepository;
import dev.eventmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static dev.eventcommon.util.TimeUtil.getNow;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventPermissionService permissionService;
    private final LocationRepository locationRepository;
    private final RegistrationRepository registrationRepository;
    private final KafkaMessageSender kafkaMessageSender;

    /**
     * Create new event
     *
     * @param createDto
     * @return
     */
    public EventDto createEvent(EventCreateRequestDto createDto) {
        log.info("Creating event '{}'", createDto.getName());

        LocationEntity dbLocation = locationRepository.findById(createDto.getLocationId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Location not found"));

        if (createDto.getMaxPlaces() > dbLocation.getCapacity()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(),
                    "Selected location has insufficient capacity");
        }

        UserEntity dbUser = userRepository.findByLogin(getLoginFromJwtToken())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "User not found"));

        LocalDateTime start = createDto.getDate();
        LocalDateTime end = createDto.getDate().plusMinutes(createDto.getDuration());
        if (eventRepository.isTimeslotBusy(createDto.getLocationId(), start, end)) {
            throw new ServiceException(HttpStatus.IM_USED.value(),
                    "Provided timeslot is not available for this location");
        };

        EventEntity toCreate = EventConverter.toEntity(createDto);
        toCreate.setOwnerId(dbUser.getId());

        EventDto eventDto = EventConverter.toDto(eventRepository.save(toCreate));

        kafkaMessageSender.send(new EventChangeMessage(
                UUID.randomUUID(),
                eventDto.getName(),
                EventType.CREATED,
                eventDto.getId(),
                getNow(),
                dbUser.getId(),
                dbUser.getId(),
                List.of(44L,45L,46L), //??
                createNewEventChanges(createDto)
        ));

        return eventDto;
    }

    /**
     * Delete the existing event and its registrations
     *
     * @param eventId
     */
    public void deleteEvent(long eventId) {
        log.info("Deleting event with id = '{}'", eventId);

        UserDto currentUser = userRepository
                .findByLogin(getLoginFromJwtToken())
                .map(UserConverter::toDto)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Current user not found"));

        EventEntity eventEntity = eventRepository.findById(eventId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Event not found"));

        if (!permissionService.canModify(currentUser, eventEntity.getOwnerId())) {
            throw new AccessDeniedException("Only the owner or admin is allowed to update an event");
        }

        eventRepository.delete(eventEntity);

        kafkaMessageSender.send(new EventChangeMessage(
                UUID.randomUUID(),
                eventEntity.getName(),
                EventType.REMOVED,
                eventId,
                getNow(),
                currentUser.getId(),
                currentUser.getId(),
                List.of(44L,45L,46L), //??
                null
        ));
    }

    /**
     * Get event info
     *
     * @param eventId
     * @return
     */
    public EventDto getEvent(long eventId) {
        log.info("Getting event with id = '{}'", eventId);
        return eventRepository.findById(eventId)
                .map(EventConverter::toDto)
                .orElseThrow(() ->
                        new ServiceException(HttpStatus.NOT_FOUND.value(), "Event not found"));
    }

    /**
     * Update event info
     *
     * @param eventId
     * @param updateDto
     * @return
     */
    public EventDto updateEvent(long eventId, EventUpdateRequestDto updateDto) {
        log.info("Updating event with id = '{}'", eventId);

        EventEntity event = eventRepository
                .findById(eventId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Event not found"));
        EventDto eventDto = EventConverter.toDto(event);

        LocationEntity dbLocation = locationRepository.findById(updateDto.getLocationId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Location not found"));

        if (updateDto.getMaxPlaces() > dbLocation.getCapacity()) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Selected location has insufficient capacity");
        }

        UserDto currentUser = userRepository
                .findByLogin(getLoginFromJwtToken())
                .map(UserConverter::toDto)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Current user not found"));

        if (!permissionService.canModify(currentUser, event.getOwnerId())) {
            throw new AccessDeniedException("Only the owner or admin is allowed to update an event");
        }

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

        EventDto saved = EventConverter.toDto(eventRepository.save(toUpdate));

        kafkaMessageSender.send(new EventChangeMessage(
                UUID.randomUUID(),
                updateDto.getName(),
                EventType.UPDATED,
                eventId,
                getNow(),
                currentUser.getId(),
                currentUser.getId(),
                List.of(44L,45L,46L), //??
                createUpdatedEventChanges(eventDto, updateDto)
        ));

        return saved;
    }

    /**
     * Find events according to the search criteria
     *
     * @param searchDto
     * @return
     */
    public List<EventDto> searchEvents(EventSearchRequestDto searchDto) {
        log.info("Searching for events according to filter criteria");
        return eventRepository.findEventsByFilterParams(
                searchDto.getName(),
                searchDto.getPlacesMin(),
                searchDto.getPlacesMax(),
                searchDto.getDateStartAfter(),
                searchDto.getDateStartBefore(),
                searchDto.getCostMin(),
                searchDto.getCostMax(),
                searchDto.getDurationMin(),
                searchDto.getDurationMax(),
                searchDto.getLocationId(),
                EventStatus.valueOf(searchDto.getEventStatus())
        ).stream().map(EventConverter::toDto).toList();
    }


    /**
     * Find events of current user
     *
     * @return
     */
    public List<EventDto> searchUserEvents() {
        log.info("Searching user's events");
        UserEntity userEntity = userRepository
                .findByLogin(getLoginFromJwtToken())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "User not found"));
        return eventRepository
                .findByOwnerId(userEntity.getId()).stream()
                .map(EventConverter::toDto)
                .toList();
    }

    /**
     * Create new event registration
     *
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

        if (event.getStatus().equals(EventStatus.CANCELLED)) {
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
     *
     * @param eventId the id of event
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
     *
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
     *
     * @return
     */
    public List<RegistrationDto> getRegistrations() {
        log.info("Getting all registrations");
        return registrationRepository.findAll().stream()
                .map(RegistrationConverter::toDto)
                .toList();
    }

    /**
     * Extract login name from the security context and jwt token
     *
     * @return
     */
    private String getLoginFromJwtToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDto userDto) {
            return userDto.getLogin();
        }
        return null;
    }

    /**
     * Create changes for the new event
     * @param createDto
     * @return
     */
    private List<ChangeItem> createNewEventChanges(EventCreateRequestDto createDto) {
        List<ChangeItem> result = new ArrayList<>();

        result.add(new ChangeItem("name", null, createDto.getName()));
        result.add(new ChangeItem("maxPlaces", null, createDto.getMaxPlaces()));
        result.add(new ChangeItem("date", null, createDto.getDate()));
        result.add(new ChangeItem("cost", null, createDto.getCost()));
        result.add(new ChangeItem("duration", null, createDto.getDuration()));
        result.add(new ChangeItem("location", null, createDto.getLocationId()));

        return result;
    }

    /**
     * Create changes for the updated event
     * @param eventDto
     * @param updateDto
     * @return
     */
    private List<ChangeItem> createUpdatedEventChanges(EventDto eventDto, EventUpdateRequestDto updateDto) {
        List<ChangeItem> result = new ArrayList<>();

        if (!updateDto.getName().equals(eventDto.getName())) {
            result.add(new ChangeItem("name", eventDto.getName(), updateDto.getName()));
        }

        if (updateDto.getMaxPlaces() != eventDto.getMaxPlaces()) {
            result.add(new ChangeItem("maxPlaces", eventDto.getMaxPlaces(), updateDto.getMaxPlaces()));
        }

        if (!updateDto.getDate().isEqual(eventDto.getDate())) {
            result.add(new ChangeItem("date", eventDto.getDate(), updateDto.getDate()));
        }

        if (updateDto.getCost().compareTo(eventDto.getCost()) != 0) {
            result.add(new ChangeItem("cost", eventDto.getCost(), updateDto.getCost()));
        }

        if (updateDto.getDuration() != eventDto.getDuration()) {
            result.add(new ChangeItem("duration", eventDto.getDuration(), updateDto.getDuration()));
        }

        if (updateDto.getLocationId() != eventDto.getLocationId()) {
            result.add(new ChangeItem("location", eventDto.getLocationId(), updateDto.getLocationId()));
        }

        return result;
    }

}
