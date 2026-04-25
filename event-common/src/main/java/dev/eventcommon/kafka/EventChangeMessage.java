package dev.eventcommon.kafka;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public record EventChangeMessage(
        UUID messageId,
        String eventName,
        EventType eventType,
        Long eventId,
        LocalDateTime occurredAt,
        Long ownerId,
        Long changedById,
        List<Long> subscribers,
        List<ChangeItem> changes) {
}
