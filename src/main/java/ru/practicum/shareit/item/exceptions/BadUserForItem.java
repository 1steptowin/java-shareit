package ru.practicum.shareit.item.exceptions;

public class BadUserForItem extends Exception {

    public BadUserForItem() {
    }

    public BadUserForItem(String message) {
        super(message);
    }
}
