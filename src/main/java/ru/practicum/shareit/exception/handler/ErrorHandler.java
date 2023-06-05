package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler({
            EmptyEmailException.class,
            EmptyItemAvailabilityException.class,
            EmptyItemDescriptionException.class,
            EmptyItemNameException.class,
            ItemUnavailableException.class,
            WrongDateException.class,
            UpdateStatusAfterApprovalException.class,
            IllegalCommentException.class,
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final RuntimeException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({
            ItemNotFoundException.class,
            UserNotFoundException.class,
            NonOwnerUpdatingException.class,
            BookingNotFoundException.class,
            BookerIsOwnerException.class,
            RequestNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final RuntimeException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleUniqueConstraintViolation(final DataIntegrityViolationException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleWrongStatusException(final WrongStatusException e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleException(final Exception e) {
        log.error(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}
