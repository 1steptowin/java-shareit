package ru.practicum.shareit.exception;

public class UpdateStatusAfterApprovalException extends RuntimeException {
    public UpdateStatusAfterApprovalException(final String message) {
        super(message);
    }
}
