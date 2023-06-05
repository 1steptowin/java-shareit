package ru.practicum.shareit.item;


import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Primary
public class ItemStorageImpl implements ItemStorage {
    private final HashMap<Integer, Item> items = new HashMap<>();
    private int nextId = 1;

    @Override
    public Item addItem(ItemDto itemDto, int userId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(nextId);
        item.setOwner(userId);
        items.put(nextId++, item);
        return getItem(nextId - 1);
    }

    @Override
    public Item updateItem(ItemDto itemDto, int itemId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(itemId);
        item.setOwner(getItem(itemId).getOwner());
        items.put(itemId, item);
        return items.get(itemId);
    }

    @Override
    public Item getItem(int itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getItems(int userId) {
        List<Item> list = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getOwner() == userId) {
                list.add(item);
            }
        }
        return list;
    }

    @Override
    public List<Item> search(String text) {
        List<Item> list = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getDescription().toLowerCase().contains(text.toLowerCase()) || item.getName().toLowerCase().contains(text.toLowerCase())) {
                if (item.getAvailable()) {
                    list.add(item);
                }
            }
        }
        return list;
    }
}
