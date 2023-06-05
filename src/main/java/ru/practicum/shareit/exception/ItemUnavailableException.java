package ru.practicum.shareit.exception;

public class ItemUnavailableException extends RuntimeException {
    public ItemUnavailableException(final String message) {
        super(message);
    }
}
