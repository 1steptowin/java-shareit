package ru.practicum.shareit.user.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.exception.UserNotFoundException;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<User> getAllUsers() {
        logger.info("Получен GET запрос /users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer userId) throws UserNotFoundException {
        logger.info("Получен GET запрос /users/{id}");
        return userService.getUserById(userId);
    }

    @PostMapping()
    public User addUser(@Valid @RequestBody User user) throws UserNotFoundException {
        logger.info("Получен POST запрос /users");
        return userService.addUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable("userId") Integer userId, @RequestBody User user) throws UserNotFoundException {
        logger.info("Получен PATCH запрос /users/{userId}");
        user.setId(userId);
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Integer userId) throws UserNotFoundException {
        logger.info("Получен DELETE запрос /users/{id}");
        userService.deleteUserById(userId);
    }
}
