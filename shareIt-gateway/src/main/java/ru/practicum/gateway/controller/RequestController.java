package ru.practicum.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.gateway.client.RequestClient;
import ru.practicum.gateway.dto.item.ItemRequestDto;
import ru.practicum.gateway.dto.item.ItemRequestWithItemsDto;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@Validated
public class RequestController {
    private final RequestClient client;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @Autowired
    public RequestController(RequestClient client) {
        this.client = client;
    }

    @PostMapping
    public Mono<ItemRequestDto> addItemRequest(@RequestHeader(USER_HEADER) Long userId,
                                               @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return client.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public Mono<List<ItemRequestWithItemsDto>> getUsersRequestsWithItems(@RequestHeader(USER_HEADER) Long userId) {
        return client.getUsersRequestsWithItems(userId);
    }

    @GetMapping("/{requestId}")
    public Mono<ItemRequestWithItemsDto> getRequestByIdWithItems(@RequestHeader(USER_HEADER) Long userId,
                                                                 @PathVariable("requestId") Long requestId) {
        return client.getRequestByIdWithItems(userId, requestId);
    }

    @GetMapping("/all")
    public Mono<List<ItemRequestWithItemsDto>> getAllRequestsOfOtherUsers(@RequestHeader(USER_HEADER) Long userId,
                                                                          @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                                          @RequestParam(required = false, defaultValue = "10") @PositiveOrZero int size) {
        return client.getAllRequestsOfOtherUsers(userId, from, size);
    }
}
