package ru.practicum.shareit.user.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Primary
@Component
public class UserStorageImpl implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();

    private int nextId = 1;

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) throws UserNotFoundException {
        try {
            return users.get(id);
        } catch (NullPointerException e) {
            throw new UserNotFoundException("Not found");
        }

    }

    @Override
    public User addUser(User user) throws UserNotFoundException {
        users.put(nextId++, user);
        User user1 = getUserById(nextId - 1);
        user1.setId(nextId - 1);
        return user1;
    }

    @Override
    public User updateUser(User user) throws UserNotFoundException {
        users.get(user.getId());
        users.put(user.getId(), user);
        return getUserById(user.getId());
    }

    @Override
    public void deleteUser(int id) throws UserNotFoundException {
        getUserById(id);
        users.remove(id);
    }
}
