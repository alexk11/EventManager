package dev.eventmanager.model.dto.event;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class EventSearchRequestDto {

    @NotBlank(message = "Event name cannot be empty")
    private String name;

    @Min(value = 1, message = "Minimum number of places must be greater than 0")
    private int placesMin;

    @Min(value = 1, message = "Maximum number of places must be greater than 0")
    private int placesMax;

    @DateTimeFormat(pattern = "YYYY-MM-DDThh:mm:ss")
    private LocalDateTime dateStartBefore;

    @DateTimeFormat(pattern = "YYYY-MM-DDThh:mm:ss")
    private LocalDateTime dateStartAfter;

    @Min(value = 1, message = "Minimum number of places must be greater than 0")
    private BigDecimal costMin;

    @Min(value = 1, message = "Minimum number of places must be greater than 0")
    private BigDecimal costMax;

    @Min(value = 1, message = "Minimum duration must be at least 1 minute")
    private int durationMin;

    @Min(value = 1, message = "Maximum duration must be greater than 1 minute")
    private int durationMax;

    @Min(value = 1, message = "Location Id must be greater than zero")
    private int locationId;

    private String eventStatus;
}
