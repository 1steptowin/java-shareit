package ru.practicum.shareit.exception;

public class BookerIsOwnerException extends RuntimeException {
    public BookerIsOwnerException(final String message) {
        super(message);
    }
}
