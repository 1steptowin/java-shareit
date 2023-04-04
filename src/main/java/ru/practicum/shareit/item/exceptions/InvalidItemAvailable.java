package ru.practicum.shareit.item.exceptions;

public class InvalidItemAvailable extends Exception {
    public InvalidItemAvailable() {
    }

    public InvalidItemAvailable(String message) {
        super(message);
    }
}
