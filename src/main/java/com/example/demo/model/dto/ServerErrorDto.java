package com.example.demo.model.dto;

import java.time.LocalDateTime;

public record ServerErrorDto(String message, String detailedMessage, LocalDateTime dateTime) { }
