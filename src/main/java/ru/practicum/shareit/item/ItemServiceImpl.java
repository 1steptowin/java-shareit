package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.BadUserForItem;
import ru.practicum.shareit.item.exceptions.InvalidItemAvailable;
import ru.practicum.shareit.item.exceptions.TextIsBlank;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.util.List;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    @Override
    public Item addItem(ItemDto itemDto, int userId) throws UserNotFoundException, InvalidItemAvailable {
        if (userService.getUserById(userId) == null) {
            throw new UserNotFoundException("Not found");
        }
        if (itemDto.getAvailable()==null) {
            throw new InvalidItemAvailable("Bad request");
        }
        return itemStorage.addItem(itemDto,userId);
    }

    @Override
    public Item updateItem(int itemId, ItemDto itemDto, int userId) throws BadUserForItem {
        if (itemStorage.getItem(itemId).getOwner() != userId) {
            throw new BadUserForItem("Bad user for item");
        } else {
            if (itemDto.getAvailable()==null) {
                itemDto.setAvailable(itemStorage.getItem(itemId).getAvailable());
            }
            if (itemDto.getName()==null) {
                itemDto.setName(itemStorage.getItem(itemId).getName());
            }
            if (itemDto.getDescription()==null) {
                itemDto.setDescription(itemStorage.getItem(itemId).getDescription());
            }
            if (itemDto.getRequest()==null) {
                itemDto.setRequest(itemStorage.getItem(itemId).getRequest());
            }
            return itemStorage.updateItem(itemDto,itemId);
        }

    }

    @Override
    public Item getItem(int itemId) {
        return (itemStorage.getItem(itemId));
    }

    @Override
    public List<Item> getItems(int userId) throws UserNotFoundException {
        userService.getUserById(userId);
        return itemStorage.getItems(userId);
    }

    @Override
    public List<Item> search(String text) throws TextIsBlank {
        if (text.isBlank()) {
            return null;
        }
        return itemStorage.search(text);
    }
}
