package dev.eventnotificator.model;

import dev.eventcommon.kafka.ChangeItem;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class KafkaEventPayloadJson {

    @NotNull(message = "Event name cannot be empty")
    private String eventName;

    @NotNull(message = "Changed id cannot be empty")
    private Long changedById;

    @NotNull(message = "Changes array cannot be null")
    private ChangeItem[] changes;
}
