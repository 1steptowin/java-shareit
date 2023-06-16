package ru.practicum.server.item.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.projection.CommentWithAuthorName;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.projection.ItemWithLastAndNextBookingAndComments;
import ru.practicum.server.exception.UserNotFoundException;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
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
    public ItemDto addItem(@RequestBody @Valid ItemDto itemDto, @RequestHeader(userIdHeader) int userId) throws UserNotFoundException {
        log.info("Получен POST запрос /items");
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto, @RequestHeader(userIdHeader) int userId, @PathVariable("itemId") int itemId) {
        log.info("Получен PATCH запрос /items/{itemID}");
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemWithLastAndNextBookingAndComments getItem(@RequestHeader(userIdHeader) int userId, @PathVariable("itemId") int itemId) {
        log.info("Получен GET запрос /items/{itemId}");
        return itemService.getItem(userId,itemId);
    }

    @GetMapping
    public List<ItemWithLastAndNextBookingAndComments> getItems(@RequestHeader(userIdHeader) int userId,
                                                                @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                                @RequestParam(required = false, defaultValue = "10") @PositiveOrZero int size) throws UserNotFoundException {
        log.info("Получен GET запрос /items");
        return itemService.getItems(userId,from,size);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text,
                                @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                @RequestParam(required = false, defaultValue = "10") @PositiveOrZero int size) {
        log.info("Получен GET запрос /items/search");
        return itemService.search(text,from,size);
    }

    @PostMapping(value = "{itemId}/comment")
    public CommentWithAuthorName addComment(@RequestHeader(userIdHeader) int userId, @PathVariable("itemId") int itemId,
                                            @RequestBody @Valid CommentDto commentDto) {
        log.info("Получен POST запрос /items/{itemId}/comment");
        return itemService.addComment(userId, itemId, commentDto);
    }
}
