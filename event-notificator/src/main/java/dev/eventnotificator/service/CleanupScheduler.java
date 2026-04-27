package dev.eventnotificator.service;

import dev.eventnotificator.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class CleanupScheduler {

    private final NotificationRepository notificationRepository;

    @Value("${scheduler.notification.days-to-keep:7}")
    private String daysToKeep;


    @Scheduled(cron = "${notification.cleanup.cron:0 0 1 * * ?}")
    public void cleanup() {
        log.info("Deleting notifications older '{}' days", daysToKeep);
        notificationRepository.deleteReadNotificationsOlderDays(Integer.parseInt(daysToKeep));
    }

}
