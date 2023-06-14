package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.projection.ItemRequestWithItems;

import java.util.stream.Collectors;
@UtilityClass
public class ItemRequestMapper {
    public ItemRequest mapDtoToModel(ItemRequestDto requestDto) {
        ItemRequest request = new ItemRequest();
        request.setDescription(requestDto.getDescription());
        return request;
    }

    public ItemRequestDto mapModelToDto(ItemRequest request) {
        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreated())
                .build();
    }

    public ItemRequestWithItemsDto mapModelToDtoWithItems(ItemRequestWithItems model) {
        return ItemRequestWithItemsDto.builder()
                .id(model.getId())
                .description(model.getDescription())
                .created(model.getCreated())
                .items(model.getItems().stream().map(ItemMapper::toItemDto).collect(Collectors.toList()))
                .build();
    }
}
