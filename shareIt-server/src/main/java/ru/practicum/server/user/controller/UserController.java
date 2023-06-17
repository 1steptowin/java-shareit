package ru.practicum.server.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.service.UserService;
import ru.practicum.server.exception.UserNotFoundException;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public List<User> getAllUsers() {
        log.info("Получен GET запрос /users");
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer userId) throws UserNotFoundException {
        log.info("Получен GET запрос /users/{id}");
        return userService.getUserById(userId);
    }

    @PostMapping()
    public User addUser(@RequestBody User user) throws UserNotFoundException {
        log.info("Получен POST запрос /users");
        return userService.addUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable("userId") Integer userId, @RequestBody User user) throws UserNotFoundException {
        log.info("Получен PATCH запрос /users/{userId}");
        user.setId(userId);
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Integer userId) throws UserNotFoundException {
        log.info("Получен DELETE запрос /users/{id}");
        userService.deleteUserById(userId);
    }
}
