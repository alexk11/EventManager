package dev.eventnotificator.model;

import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
public class NotificationDto {

    @NotNull(message = "Id cannot be empty")
    private Long id;

    @NotNull(message = "User id cannot be empty")
    private Long userId;

    private boolean isRead;

    @Past(message = "Creation date must be in the past")
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    @NotNull(message = "Payload id cannot be null")
    private Long payloadId;
}
