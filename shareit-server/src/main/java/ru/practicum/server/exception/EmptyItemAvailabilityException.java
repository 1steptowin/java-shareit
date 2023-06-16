package ru.practicum.server.exception;

public class EmptyItemAvailabilityException extends RuntimeException {
    public EmptyItemAvailabilityException(final String message) {
        super(message);
    }
}