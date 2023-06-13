package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.exceptions.UserDuplicateException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(Integer userId) throws UserNotFoundException;

    User addUser(User user) throws UserDuplicateException, UserNotFoundException;

    User updateUser(User user) throws UserNotFoundException, UserDuplicateException;

    void deleteUserById(Integer userId) throws UserNotFoundException;

    void checkUser(int userId);
}
