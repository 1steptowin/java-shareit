package ru.practicum.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.gateway.client.ItemClient;
import ru.practicum.gateway.dto.comments.CommentWithAuthorName;
import ru.practicum.gateway.dto.comments.CommentsDto;
import ru.practicum.gateway.dto.item.ItemDto;
import ru.practicum.gateway.dto.item.ItemWithLastAndNextBookingAndComments;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemClient client;
    private static final String SPECIFIC_ITEM_PATH = "/{id}";
    private static final String SEARCH_PATH = "/search";
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemClient client) {
        this.client = client;
    }

    @PostMapping
    public Mono<ItemDto> addItem(@RequestHeader(USER_HEADER) Long userId, @RequestBody @Valid ItemDto itemDto) {
        return client.addItem(userId, itemDto);
    }

    @PostMapping(value = SPECIFIC_ITEM_PATH + "/comment")
    public Mono<CommentWithAuthorName> addComment(@RequestHeader(USER_HEADER) Long userId, @PathVariable("id") Long itemId,
                                                  @RequestBody @Valid CommentsDto commentsDto) {
        return client.addComment(userId, itemId, commentsDto);
    }

    @PatchMapping(value = SPECIFIC_ITEM_PATH)
    public Mono<ItemDto> updateItem(@RequestHeader(USER_HEADER) Long ownerId, @PathVariable("id") Long itemId,
                                    @RequestBody ItemDto itemDto) {
        return client.updateItem(ownerId, itemId, itemDto);
    }

    @GetMapping(SPECIFIC_ITEM_PATH)
    public Mono<ItemWithLastAndNextBookingAndComments> getItemById(@RequestHeader(USER_HEADER) Long userId, @PathVariable("id") Long itemId) {
        return client.getItemById(userId, itemId);
    }

    @GetMapping
    public Mono<List<ItemWithLastAndNextBookingAndComments>> getOwnersItems(@RequestHeader(USER_HEADER) Long ownerId,
                                                                            @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                                            @RequestParam(required = false, defaultValue = "10") @PositiveOrZero int size) {
        return client.getAllOwnersItems(ownerId,from,size);
    }

    @GetMapping(SEARCH_PATH)
    public Mono<List<ItemDto>> searchItems(@RequestParam String text,
                                           @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                           @RequestParam(required = false, defaultValue = "10") @PositiveOrZero int size) {
        return client.searchItems(text,from,size);
    }
}
