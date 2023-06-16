package ru.practicum.server.exception;

public class EmptyEmailException extends RuntimeException {
    public EmptyEmailException(final String message) {
        super(message);
    }
}

