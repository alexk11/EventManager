package dev.eventmanager.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;


@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class LocationDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 0;

    @NotNull(message = "Location id cannot be empty")
    private Long id;

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Address cannot be empty")
    private String address;

    @Min(value = 5, message = "Capacity must be at least 5")
    private int capacity;

    private String description;
}
