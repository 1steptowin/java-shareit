package ru.practicum.server.exception;

public class ItemUnavailableException extends RuntimeException {
    public ItemUnavailableException(final String message) {
        super(message);
    }
}
