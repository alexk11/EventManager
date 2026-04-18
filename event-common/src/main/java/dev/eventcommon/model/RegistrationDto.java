package dev.eventcommon.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
public class RegistrationDto {

    @NotNull(message = "Registration id cannot be null")
    private Long id;

    @NotNull(message = "Event id cannot be null")
    private Long eventId;

    @NotNull(message = "User id cannot be null")
    private Long userId;

    @NotNull(message = "Registration date cannot be null")
    @DateTimeFormat(pattern = "YYYY-MM-DDThh:mm:ss")
    private LocalDateTime registrationDate;
}
