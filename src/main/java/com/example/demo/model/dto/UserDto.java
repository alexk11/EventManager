package com.example.demo.model.dto;

import com.example.demo.model.Role;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@Builder
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class UserDto {

    @Min(value = 1, message = "User id must be greater than zero")
    private Long id;

    @NotBlank(message = "Login cannot be empty")
    private String login;

    @Positive(message = "Age must be greater than zero")
    private int age;

    @NotNull(message = "Role cannot be empty")
    private Role role;
}
