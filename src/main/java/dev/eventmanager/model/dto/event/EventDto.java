package dev.eventmanager.model.dto.event;

import dev.eventmanager.model.EventStatus;
import dev.eventmanager.model.dto.RegistrationDto;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class EventDto {

    @NotNull(message = "Id cannot be empty")
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Owner id cannot be empty")
    private Long ownerId;

    @Min(value = 1, message = "Number of places must be greater than 0")
    private Integer maxPlaces;

    @Min(value = 0, message = "Number of places must not be negative")
    private int occupiedPlaces;

    @Future(message = "Event date must be in the future")
    @DateTimeFormat(pattern = "YYYY-MM-DDThh:mm:ss")
    private LocalDateTime date;

    @Min(value = 0, message = "Cost must not be negative")
    private BigDecimal cost;

    @Min(value = 30, message = "Duration must be at least 30 minutes")
    private int duration;

    @NotBlank(message = "Location cannot be empty")
    private int locationId;

    private EventStatus status;

    private List<RegistrationDto> registrations;
}
