package dev.eventmanager.controller;

import dev.eventmanager.model.JwtResponse;
import dev.eventmanager.model.UserCredentials;
import dev.eventmanager.model.dto.UserDto;
import dev.eventmanager.model.dto.UserRegistrationDto;
import dev.eventmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDto> registerUser(
            @RequestBody @Valid UserRegistrationDto registrationDto) {
        log.info("POST request to register user: userDto = {}", registrationDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createUser(registrationDto));
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUser(
            @PathVariable long userId) {
        log.info("GET request to get user: userId = {}", userId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.getUser(userId));
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtResponse> authUser(
            @RequestBody @Valid UserCredentials userCredentials) {
        log.info("POST request to authenticate user: userCredentials = {}",
                userCredentials);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(userService.authUser(userCredentials));
    }

}
