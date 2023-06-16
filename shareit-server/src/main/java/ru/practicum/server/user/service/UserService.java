package ru.practicum.server.user.service;

import ru.practicum.server.user.model.User;
import ru.practicum.server.exception.UserNotFoundException;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(Integer userId) throws UserNotFoundException;

    User addUser(User user) throws UserNotFoundException;

    User updateUser(User user) throws UserNotFoundException;

    void deleteUserById(Integer userId) throws UserNotFoundException;
}
