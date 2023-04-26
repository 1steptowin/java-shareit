package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.util.Collection;

public interface UserStorage {
    Collection<User> getAllUsers();

    User getUserById(int id) throws UserNotFoundException;

    User addUser(User user) throws UserNotFoundException;

    User updateUser(User user) throws UserNotFoundException;

    void deleteUser(int id) throws UserNotFoundException;
}
