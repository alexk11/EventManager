package dev.eventmanager.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class UserRegistrationDto {

    @NotEmpty(message = "Login cannot be empty")
    @Size(min = 5)
    private String login;

    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 5, max = 20)
    private String password;

    @Positive(message = "Age must be greater than zero")
    private int age;
}
