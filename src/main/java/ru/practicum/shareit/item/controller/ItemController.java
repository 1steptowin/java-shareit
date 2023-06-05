package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.projection.CommentWithAuthorName;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.BadUserForItem;
import ru.practicum.shareit.item.exceptions.InvalidItemAvailable;
import ru.practicum.shareit.item.exceptions.TextIsBlank;
import ru.practicum.shareit.item.projection.ItemWithLastAndNextBookingAndComments;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {

    private static final String userIdHeader = "X-Sharer-User-Id";
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader(userIdHeader) int userId) throws UserNotFoundException, InvalidItemAvailable {
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @RequestHeader(userIdHeader) int userId, @PathVariable("itemId") int itemId) throws BadUserForItem {
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithLastAndNextBookingAndComments getItem(@RequestHeader(userIdHeader) int userId, @PathVariable("itemId") int itemId) {
        return itemService.getItem(userId,itemId);
    }

    @GetMapping
    public List<ItemWithLastAndNextBookingAndComments> getItems(@RequestHeader(userIdHeader) int userId) throws UserNotFoundException {
        return itemService.getItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text) throws TextIsBlank {
        return itemService.search(text);
    }
    @PostMapping(value =  "{itemId}/comment")
    public CommentWithAuthorName addComment(@RequestHeader(userIdHeader) int userId, @PathVariable("itemId") int itemId,
                                            @RequestBody @Valid CommentDto commentDto) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
