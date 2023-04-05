package ru.practicum.shareit.user.exceptions;

public class UserDuplicateException extends Exception {
    public UserDuplicateException() {
    }

    public UserDuplicateException(String message) {
        super(message);
    }
}
