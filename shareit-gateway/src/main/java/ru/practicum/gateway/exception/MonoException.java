package ru.practicum.gateway.exception;

import org.springframework.http.HttpStatus;

public class MonoException extends RuntimeException {
    private final HttpStatus status;

    public MonoException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public int getStatusCode() {
        return status.value();
    }
}
