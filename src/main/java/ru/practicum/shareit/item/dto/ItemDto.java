package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ItemDto {
    @NotBlank
    String name;
    @NotBlank
    String description;
    Boolean available;
    Integer request;

}
