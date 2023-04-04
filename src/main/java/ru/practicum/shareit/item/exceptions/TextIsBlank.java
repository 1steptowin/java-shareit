package ru.practicum.shareit.item.exceptions;

public class TextIsBlank extends Exception{
    public TextIsBlank() {
    }

    public TextIsBlank(String message) {
        super(message);
    }
}
