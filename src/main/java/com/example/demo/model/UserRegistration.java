package com.example.demo.model;

import jakarta.validation.constraints.NotEmpty;
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
public class UserRegistration {

    @NotEmpty(message = "Login cannot be empty")
    private String login;

    @NotEmpty(message = "Password cannot be empty")
    private String password;

    @Positive(message = "Age must be greater than zero")
    private int age;
}
