package ru.practicum.gateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GatewayExceptionHandler {

    @ExceptionHandler(MonoException.class)
    public ResponseEntity<Object> handleMonoException(MonoException e) {
        return ResponseEntity.status(e.getStatusCode())
                .body(e.getMessage());
    }

    @ExceptionHandler(WrongStatusException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public GatewayErrorResponse handleWrongStatusException(WrongStatusException e) {
        return new GatewayErrorResponse(e.getMessage());
    }
}
