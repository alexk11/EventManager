package dev.eventnotificator.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "notification_event_payloads")
public class NotificationEventPayloadEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID messageId;

    @Column(nullable = false)
    private String eventType;

    @Column(nullable = false)
    private Long eventId;

    @Column(nullable = false)
    private LocalDateTime occurredAt;

    private Long ownerId;

    private Long changedById;

    @Column(nullable = false, columnDefinition = "jsonb")
    @ColumnTransformer(write = "?::jsonb")
    private String payloadJson;
}