package com.example.demo.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class LocationDto {

    @NotNull(message = "Location id cannot be empty")
    private long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Address cannot be empty")
    private String address;

    @Min(value = 1, message = "Capacity must be greater than 0")
    private int capacity;

    private String description;
}
