package ru.practicum.server.request.service;

import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestWithItemsDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto addItemRequest(int userId, ItemRequestDto itemRequestDto);

    List<ItemRequestWithItemsDto> getUsersRequestsWithItems(int userId);

    ItemRequestWithItemsDto getRequestByIdWithItems(int userId, Long requestId);

    List<ItemRequestWithItemsDto> getAllRequestsOfOtherUsers(int userId, int from, int size);
}

