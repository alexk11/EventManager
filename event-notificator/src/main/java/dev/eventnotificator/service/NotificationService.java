package dev.eventnotificator.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.eventcommon.kafka.ChangeItem;
import dev.eventcommon.kafka.EventChangeMessage;
import dev.eventnotificator.converter.NotificationConverter;
import dev.eventnotificator.entity.NotificationEventPayloadEntity;
import dev.eventnotificator.model.KafkaEventPayloadJson;
import dev.eventnotificator.model.NotificationDto;
import dev.eventnotificator.model.NotificationEventPayloadDto;
import dev.eventnotificator.repository.NotificationRepository;
import dev.eventnotificator.repository.PayloadRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final PayloadRepository payloadRepository;
    private final NotificationCounterService notificationCounterService;

    @Value("${security.jwt.secret-key:secret-key}")
    private String secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    @Transactional
    public void saveEvent(EventChangeMessage msg) {
        log.info("Saving kafka message to db, message = {}", msg);

        // use (eventId, ownerId) as idempotency key
        if (payloadRepository.existsByEventIdAndOwnerId(msg.eventId(), msg.ownerId())) {
            log.info("Event payload is already in db: eventId = {}, userId = {}",
                    msg.eventId(), msg.ownerId());
            return;
        }

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

        notificationCounterService.incrementUnread(msg.ownerId(), 1);
    }


    public List<NotificationDto> getUserNotifications(String token) {
        log.info("Get user's notifications");
        Long userId = validateAndGetUserId(token);
        return notificationRepository.findByUserId(userId).stream()
                .map(NotificationConverter::toNotificationDto)
                .toList();
    }


    @Transactional
    public List<Long> markAsRead(String token, List<Long> notificationIds) {
        log.info("Mark user's notifications as read");

        Long userId = validateAndGetUserId(token);

        Set<Long> idsSet = new HashSet<>(notificationIds);
        List<Long> markIds = notificationRepository.findByUserIdAndIsReadFalse(userId).stream()
                .toList().stream()
                .filter(idsSet::contains)
                .toList();
        notificationRepository.markAsReadByIdsAndUserId(userId, markIds);

        notificationCounterService.syncUnreadFromDatabase(userId);

        return markIds;
    }


    private String getEventPayload(EventChangeMessage msg) {
        try {
            KafkaEventPayloadJson payload = KafkaEventPayloadJson.builder()
                    .eventName(msg.eventName())
                    .changedById(msg.changedById())
                    .changes(msg.changes().toArray(ChangeItem[]::new))
                    .build();
            return new ObjectMapper().writeValueAsString(payload);
        } catch (JsonProcessingException ex) {
            log.error("Convert object to string error: {}", ex.getMessage());
        }
        return null;
    }


    private Long validateAndGetUserId(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm)
                .build();
        String jwt = token.split("\\s")[1];
        DecodedJWT decoded = verifier.verify(jwt);

        return decoded.getClaim("userId").asLong();
    }

}
