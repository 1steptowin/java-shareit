package ru.practicum.server.exception;

public class BookerIsOwnerException extends RuntimeException {
    public BookerIsOwnerException(final String message) {
        super(message);
    }
}
