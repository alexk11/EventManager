package dev.eventmanager.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record ErrorMessageResponse(
        String message,
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        LocalDateTime dateTime
) {}
