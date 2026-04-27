package dev.eventnotificator.controller;

import dev.eventnotificator.model.NotificationDto;
import dev.eventnotificator.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<NotificationDto>> getNotifications(
            @RequestHeader("Authorization") String jwtToken) {
        log.info("GET request to get all user's notifications");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notificationService.getUserNotifications(jwtToken));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<Long>> markAsRead(
            @RequestHeader("Authorization") String jwtToken,
            @RequestBody List<Long> notificationIds) {
        log.info("POST request to mark user's notifications as read");
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notificationService.markAsRead(jwtToken, notificationIds));
    }

}
