package ru.practicum.shareit.item.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;

/**
 * TODO Sprint add-controllers.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class Item {
    int id;

    String name;
    String description;
    Boolean available;
    int owner;
    Integer request;

    public Item(String name, String description, Boolean available,Integer request) {
        this.name=name;
        this.description=description;
        this.available=available;
        this.request=request;
    }
}
