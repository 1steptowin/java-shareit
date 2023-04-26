package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.BadUserForItem;
import ru.practicum.shareit.item.exceptions.InvalidItemAvailable;
import ru.practicum.shareit.item.exceptions.TextIsBlank;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.util.List;

public interface ItemService {

    Item addItem(ItemDto itemDto, int userId) throws UserNotFoundException, InvalidItemAvailable;

    Item updateItem(int itemId, ItemDto itemDto, int userId) throws BadUserForItem;

    Item getItem(int itemId);

    List<Item> getItems(int userId) throws UserNotFoundException;

    List<Item> search(String text) throws TextIsBlank;
}
