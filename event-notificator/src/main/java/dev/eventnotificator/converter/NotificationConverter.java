package dev.eventnotificator.converter;

import dev.eventcommon.kafka.EventType;
import dev.eventnotificator.entity.NotificationEntity;
import dev.eventnotificator.entity.NotificationEventPayloadEntity;
import dev.eventnotificator.model.NotificationDto;
import dev.eventnotificator.model.NotificationEventPayloadDto;


public class NotificationConverter {

    public static NotificationDto toNotificationDto(NotificationEntity entity) {
        return NotificationDto.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .isRead(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .readAt(entity.getReadAt())
                .payloadId(entity.getPayloadId())
                .build();
    }

    public static NotificationEntity toNotificationEntity(NotificationDto dto) {
        return NotificationEntity.builder()
                .id(dto.getId())
                .userId(dto.getUserId())
                .isRead(dto.isRead())
                .createdAt(dto.getCreatedAt())
                .readAt(dto.getReadAt())
                .payloadId(dto.getPayloadId())
                .build();
    }

    public static NotificationEventPayloadDto toEventPayloadDto(NotificationEventPayloadEntity entity) {
        return NotificationEventPayloadDto.builder()
                .id(entity.getId())
                .messageId(entity.getMessageId())
                .eventType(EventType.valueOf(entity.getEventType()))
                .eventId(entity.getEventId())
                .occurredAt(entity.getOccurredAt())
                .ownerId(entity.getOwnerId())
                .changedById(entity.getChangedById())
                .payloadJson(entity.getPayloadJson())
                .build();
    }

    public static NotificationEventPayloadEntity toEventPayloadEntity(NotificationEventPayloadDto dto) {
        return NotificationEventPayloadEntity.builder()
                .id(dto.getId())
                .messageId(dto.getMessageId())
                .eventType(dto.getEventType().name())
                .eventId(dto.getEventId())
                .occurredAt(dto.getOccurredAt())
                .ownerId(dto.getOwnerId())
                .changedById(dto.getChangedById())
                .payloadJson(dto.getPayloadJson())
                .build();
    }

}
