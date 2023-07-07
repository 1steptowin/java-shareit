package ru.practicum.server.exception;

public class EmptyItemNameException extends RuntimeException {
    public EmptyItemNameException(final String message) {
        super(message);
    }
}