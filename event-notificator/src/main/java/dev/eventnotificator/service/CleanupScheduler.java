package dev.eventnotificator.service;

import dev.eventnotificator.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;


@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class CleanupScheduler {

    @Value("${app.notification.days-to-keep:7}")
    private String daysToKeep;

    private final NotificationRepository notificationRepository;

    @Scheduled(cron = "${event.status.cron:0 */3 * * * *}")
    public void cleanup() {
        log.info("Deleting notifications older than '{}' days", daysToKeep);
        notificationRepository.deleteReadNotificationsOlderDays(Integer.parseInt(daysToKeep));
    }

}
