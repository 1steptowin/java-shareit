package ru.practicum.shareit.exception;

public class WrongStatusException extends RuntimeException {
    public WrongStatusException(final String message) {
        super(message);
    }
}
