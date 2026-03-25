package dev.eventmanager.model.dto;

import java.time.LocalDateTime;

public record ServerErrorDto(String message, String detailedMessage, LocalDateTime dateTime) { }
