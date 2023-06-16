package ru.practicum.server.exception;

public class EmptyItemDescriptionException extends RuntimeException {
    public EmptyItemDescriptionException(final String message) {
        super(message);
    }
}
