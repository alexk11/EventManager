package dev.eventnotificator.service;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.eventcommon.kafka.ChangeItem;
import dev.eventcommon.kafka.EventChangeMessage;
import dev.eventnotificator.converter.NotificationConverter;
import dev.eventnotificator.entity.NotificationEntity;
import dev.eventnotificator.entity.NotificationEventPayloadEntity;
import dev.eventnotificator.model.KafkaEventPayloadJson;
import dev.eventnotificator.model.NotificationDto;
import dev.eventnotificator.model.NotificationEventPayloadDto;
import dev.eventnotificator.repository.NotificationRepository;
import dev.eventnotificator.repository.PayloadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

import static dev.eventcommon.util.TimeUtil.getNow;


@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PayloadRepository payloadRepository;

    @Transactional
    public void saveEvent(EventChangeMessage msg) {
        log.info("Saving kafka message into db, message = {}", msg);

        NotificationEventPayloadDto payloadDto = NotificationEventPayloadDto.builder()
                .messageId(msg.messageId())
                .eventType(msg.eventType())
                .eventId(msg.eventId())
                .occurredAt(msg.occurredAt())
                .ownerId(msg.ownerId())
                .changedById(msg.changedById())
                .payloadJson(getEventPayload(msg))
                .build();
        NotificationEventPayloadEntity saved =
                payloadRepository.save(NotificationConverter.toEventPayloadEntity(payloadDto));

        NotificationDto notificationDto = NotificationDto.builder()
                .userId(msg.ownerId())
                .isRead(false)
                .createdAt(msg.occurredAt())
                .payloadId(saved.getId())
                .build();
        notificationRepository.save(NotificationConverter.toNotificationEntity(notificationDto));
    }

    public List<NotificationDto> getUserNotifications(String token) {
        String jwt = token.split("\\s")[1];
        Long userId = JWT.decode(jwt).getClaim("userId").asLong();
        return notificationRepository.findByUserId(userId).stream()
                .map(NotificationConverter::toNotificationDto)
                .toList();
    }

    public List<NotificationDto> markAsRead(String token, List<Long> notificationIds) {
        String jwt = token.split("\\s")[1];
        Long userId = JWT.decode(jwt).getClaim("userId").asLong();

        Set<Long> toUpdate = new HashSet<>(notificationIds);

        List<Long> common = notificationRepository.findByUserId(userId).stream()
                .map(NotificationEntity::getId)
                .toList().stream()
                .filter(toUpdate::contains)
                .toList();

        List<NotificationDto> updated = new ArrayList<>();
        common.forEach(id ->
                notificationRepository.findById(id).ifPresent(item -> {
                    item.setRead(true);
                    item.setReadAt(getNow());
                    notificationRepository.save(item);
                    updated.add(NotificationConverter.toNotificationDto(item));
                }));

        return updated;
    }

    private String getEventPayload(EventChangeMessage msg) {
        try {
            KafkaEventPayloadJson payload = KafkaEventPayloadJson.builder()
                    .eventName(msg.eventName())
                    .changedById(msg.changedById())
                    .changes(msg.changes().toArray(ChangeItem[]::new))
                    .build();
            return new ObjectMapper().writeValueAsString(payload);
        } catch(JsonProcessingException ex) {
            log.error("Convert object to string error: {}", ex.getMessage());
        }
        return null;
    }

}
