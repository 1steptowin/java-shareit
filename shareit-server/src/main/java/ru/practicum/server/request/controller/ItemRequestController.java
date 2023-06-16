package ru.practicum.server.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestWithItemsDto;
import ru.practicum.server.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final RequestService requestService;
    private static final String userIdHeader = "X-Sharer-User-Id";

    @Autowired
    public ItemRequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping
    public ItemRequestDto addItemRequest(@RequestHeader(userIdHeader) int userId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Получен POST запрос /requests");
        return requestService.addItemRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestWithItemsDto> getUsersRequestsWithItems(@RequestHeader(userIdHeader) int userId) {
        log.info("Получен GET запрос /requests");
        return requestService.getUsersRequestsWithItems(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestWithItemsDto getRequestByIdWithItems(@RequestHeader(userIdHeader) int userId,
                                                           @PathVariable("requestId") Long requestId) {
        log.info("Получен POST запрос /requests/{requestId}");
        return requestService.getRequestByIdWithItems(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestWithItemsDto> getAllRequestsOfOtherUsers(@RequestHeader(userIdHeader) int userId,
                                                                    @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                                    @RequestParam(required = false, defaultValue = "10") @PositiveOrZero int size) {
        log.info("Получен GET запрос /requests/all");
        return requestService.getAllRequestsOfOtherUsers(userId, from, size);
    }
}
