package dev.eventnotificator.model;

import dev.eventcommon.kafka.EventType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@Builder
public class NotificationEventPayloadDto {

    @NotNull(message = "Id cannot be empty")
    private Long id;

    @NotNull(message = "Message id cannot be empty")
    private UUID messageId;

    private EventType eventType;

    @NotNull(message = "Event id cannot be empty")
    private Long eventId;

    @Past(message = "Occurrence date must be in the past")
    private LocalDateTime occurredAt;

    @NotNull(message = "Owner id cannot be empty")
    private Long ownerId;

    private Long changedById;

    @NotNull(message = "Payload cannot be null")
    private String payloadJson;
}
