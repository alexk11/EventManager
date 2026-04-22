package dev.eventnotificator.repository;

import dev.eventnotificator.entity.NotificationEventPayloadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PayloadRepository extends JpaRepository<NotificationEventPayloadEntity, Long> {
    boolean existsByEventIdAndOwnerId(long eventId, long ownerId);
}
