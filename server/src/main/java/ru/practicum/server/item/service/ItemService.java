package ru.practicum.server.item.service;

import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.projection.CommentWithAuthorName;
import ru.practicum.server.item.projection.ItemWithLastAndNextBookingAndComments;
import ru.practicum.server.exception.UserNotFoundException;

import java.util.List;

public interface ItemService {

    ItemDto addItem(ItemDto itemDto, int userId) throws UserNotFoundException;

    ItemDto updateItem(int itemId,ItemDto itemDto, int userId);

    ItemWithLastAndNextBookingAndComments getItem(int userdId, int itemId);

    List<ItemWithLastAndNextBookingAndComments> getItems(int userId, int from, int size) throws UserNotFoundException;

    List<ItemDto> search(String text, int from, int size);

    CommentWithAuthorName addComment(int userId, int itemId, CommentDto commentDto);
}
