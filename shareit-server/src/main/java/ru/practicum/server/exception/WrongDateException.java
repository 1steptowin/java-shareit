package ru.practicum.server.exception;

public class WrongDateException extends RuntimeException {
    public WrongDateException(final String message) {
        super(message);
    }
}
