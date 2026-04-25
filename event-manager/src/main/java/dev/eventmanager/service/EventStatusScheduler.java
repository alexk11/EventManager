package dev.eventmanager.service;

import dev.eventmanager.model.EventStatus;
import dev.eventmanager.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;


@Slf4j
@Configuration
@RequiredArgsConstructor
public class EventStatusScheduler {

    private static final String CACHE_KEY_PREFIX = "event:";
    private final RedisCacheManager cacheManager;
    private final EventRepository eventRepository;

    @Scheduled(cron = "${event.status.cron:0 */3 * * * *}")
    public void updateStatuses() {

        log.info("Updating the status of started events");
        List<Long> startedIds = eventRepository
                .findStartedEventsWithStatus(EventStatus.WAIT_START);
        for (Long eventId : startedIds) {
            eventRepository.changeStatus(eventId, EventStatus.STARTED);
            cacheManager.getCache(CACHE_KEY_PREFIX).evict("id:" + eventId);
        }

        log.info("Updating the status of finished events");
        List<Long> finishedIds = eventRepository
                .findFinishedEventsWithStatus(EventStatus.STARTED);
        for (Long eventId : finishedIds) {
            eventRepository.changeStatus(eventId, EventStatus.FINISHED);
            cacheManager.getCache(CACHE_KEY_PREFIX).evict("id:" + eventId);
        }
    }

}
