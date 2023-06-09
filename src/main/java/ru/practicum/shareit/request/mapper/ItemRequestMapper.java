package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.projection.ItemRequestWithItems;

import java.util.stream.Collectors;

public class ItemRequestMapper {
    public static ItemRequest mapDtoToModel(ItemRequestDto requestDto) {
        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        return request;
    }

    public static ItemRequestDto mapModelToDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }

    public static ItemRequestWithItemsDto mapModelToDtoWithItems(ItemRequestWithItems model) {
        return ItemRequestWithItemsDto.builder()
                .id(model.getId())
                .description(model.getDescription())
                .created(model.getCreated())
                .items(model.getItems().stream().map(ItemMapper::toItemDto).collect(Collectors.toList()))
                .build();
    }
}
