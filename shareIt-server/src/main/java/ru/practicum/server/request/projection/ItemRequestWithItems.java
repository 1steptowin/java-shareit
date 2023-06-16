package ru.practicum.server.request.projection;

import ru.practicum.server.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRequestWithItems {
    Long getId();

    String getDescription();

    LocalDateTime getCreated();

    List<Item> getItems();
}