package ru.practicum.server.exception;

public class UpdateStatusAfterApprovalException extends RuntimeException {
    public UpdateStatusAfterApprovalException(final String message) {
        super(message);
    }
}
