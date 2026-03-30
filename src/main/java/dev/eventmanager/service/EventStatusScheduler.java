package dev.eventmanager.service;

import dev.eventmanager.model.EventStatus;
import dev.eventmanager.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
@RequiredArgsConstructor
public class EventStatusScheduler {

    private final EventRepository eventRepository;

    @Scheduled(cron = "${event.status.cron:0 */3 * * * *}")
    public void updateStatuses() {

        log.info("Updating the status of started events");
        eventRepository
                .findStartedEventsWithStatus(EventStatus.WAIT_START)
                .forEach(id ->
                        eventRepository.changeStatus(id, EventStatus.STARTED));

        log.info("Updating the status of finished events");
        eventRepository
                .findFinishedEventsWithStatus(EventStatus.STARTED)
                .forEach(id ->
                        eventRepository.changeStatus(id, EventStatus.FINISHED));
    }

}
