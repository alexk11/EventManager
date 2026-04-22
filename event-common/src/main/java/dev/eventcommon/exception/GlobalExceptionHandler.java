package dev.eventcommon.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = { ServiceException.class })
    protected ResponseEntity<ErrorMessageResponse> handleServiceException(ServiceException ex) {
        log.error("Service exception occurred: {}", ex.getMessage(), ex);
        var error = new ErrorMessageResponse(
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(error);
    }

    @ExceptionHandler(value = { Exception.class })
    protected ResponseEntity<ErrorMessageResponse> handleGenericException(Exception ex) {
        log.error("Exception occurred: {}", ex.getMessage(), ex);
        var error = new ErrorMessageResponse(
                ex.getMessage(),
                LocalDateTime.now());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

}
