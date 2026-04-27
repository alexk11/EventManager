package dev.eventmanager.model.dto.event;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class EventUpdateRequestDto {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Positive(message = "Maximum number of places must be greater than zero")
    private int maxPlaces;

    @Future(message = "Event date must be in the future")
    @DateTimeFormat(pattern = "YYYY-MM-DDThh:mm:ss")
    private LocalDateTime date;

    @Min(value = 0, message = "Cost must not be negative")
    private BigDecimal cost;

    @Min(value = 30, message = "Duration must be at least 30 minutes")
    private int duration;

    @Min(value = 1, message = "Location id must be greater than zero")
    private Long locationId;
}
