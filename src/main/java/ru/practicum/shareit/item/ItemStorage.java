package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    Item addItem(ItemDto itemDto, int userId);

    Item updateItem(ItemDto itemDto, int itemId);

    Item getItem(int itemId);

    List<Item> getItems(int userId);

    List<Item> search(String text);
}
