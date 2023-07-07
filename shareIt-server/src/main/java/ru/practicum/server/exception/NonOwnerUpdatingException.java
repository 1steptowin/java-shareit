package ru.practicum.server.exception;

public class NonOwnerUpdatingException extends RuntimeException {
    public NonOwnerUpdatingException(final String message) {
        super(message);
    }
}
