package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.BadUserForItem;
import ru.practicum.shareit.item.exceptions.InvalidItemAvailable;
import ru.practicum.shareit.item.exceptions.TextIsBlank;
import ru.practicum.shareit.item.projection.ItemWithLastAndNextBookingAndComments;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, int userId) throws UserNotFoundException, InvalidItemAvailable;

    ItemDto updateItem(int itemId,ItemDto itemDto, int userId) throws BadUserForItem;

    ItemWithLastAndNextBookingAndComments getItem(int userdId, int itemId);

    List<ItemWithLastAndNextBookingAndComments> getItems(int userId) throws UserNotFoundException;

    List<ItemDto> search(String text) throws TextIsBlank;
}
