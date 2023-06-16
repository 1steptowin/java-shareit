package ru.practicum.server.exception;

public class IllegalCommentException extends RuntimeException {
    public IllegalCommentException(final String message) {
        super(message);
    }
}