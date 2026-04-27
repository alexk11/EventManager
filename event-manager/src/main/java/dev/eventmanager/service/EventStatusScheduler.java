package dev.eventmanager.service;

import dev.eventmanager.model.EventStatus;
import dev.eventmanager.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class EventStatusScheduler {

    private final EventService eventService;
    private final EventRepository eventRepository;

    @Scheduled(cron = "${event.status.cron:0 */30 * * * *}")
    @Transactional
    public void updateStatuses() {

        log.info("Updating the status of started events");
        eventRepository.updateStartedEventsWithStatus(
                EventStatus.WAIT_START, EventStatus.STARTED);

        List<Long> startedIds = eventRepository
                .findStartedEventsWithStatus(EventStatus.WAIT_START);
        for (Long eventId : startedIds) {
            eventService.evictFromCache(eventId);
        }

        log.info("Updating the status of finished events");
        eventRepository.updateFinishedEventsWithStatus(
                EventStatus.STARTED, EventStatus.FINISHED);

        List<Long> finishedIds = eventRepository
                .findFinishedEventsWithStatus(EventStatus.STARTED);
        for (Long eventId : finishedIds) {
            eventService.evictFromCache(eventId);
        }
    }

}
