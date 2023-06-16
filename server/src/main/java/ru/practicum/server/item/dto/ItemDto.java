package ru.practicum.server.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ItemDto {
    int id;
    String name;
    String description;
    Boolean available;
    Long requestId;
}
