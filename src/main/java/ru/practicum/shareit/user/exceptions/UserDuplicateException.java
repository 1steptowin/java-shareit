package ru.practicum.shareit.user.exceptions;

public class UserDuplicateException extends Exception {
    public UserDuplicateException() {
    }

    UserDuplicateException(String message) {
        super(message);
    }
}
