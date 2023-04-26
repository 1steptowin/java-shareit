package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exceptions.BadUserForItem;
import ru.practicum.shareit.item.exceptions.InvalidItemAvailable;
import ru.practicum.shareit.item.exceptions.TextIsBlank;
import ru.practicum.shareit.user.exceptions.ErrorResponse;

@RestControllerAdvice
@Slf4j
public class ItemErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBadUserForItemException(final BadUserForItem e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidItemAvailableException(final InvalidItemAvailable e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleTextIsBlankException(final TextIsBlank e) {
        return new ErrorResponse(e.getMessage());
    }
}
