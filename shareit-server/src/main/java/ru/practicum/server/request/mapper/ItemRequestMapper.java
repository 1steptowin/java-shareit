package ru.practicum.server.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.server.item.mapper.ItemMapper;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestWithItemsDto;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.projection.ItemRequestWithItems;

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