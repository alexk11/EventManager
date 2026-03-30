package dev.eventmanager.model.dto.event;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;


@Getter
@Setter
@Builder
public class EventCreateRequestDto {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @Max(value = 10, message = "Maximum number of tickets in one request must not exceed 10")
    private int maxPlaces;

    @Future(message = "Event date must be in the future")
    @DateTimeFormat(pattern = "YYYY-MM-DDThh:mm:ss")
    private LocalDateTime date;

    @Min(value = 0, message = "Cost must not be negative")
    private BigDecimal cost;

    @Min(value = 30, message = "Duration must be at least 30 minutes")
    private int duration;

    @Min(value = 1, message = "Location Id must be greater than zero")
    private long locationId;
}
