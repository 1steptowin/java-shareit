package ru.practicum.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.gateway.client.UserClient;
import ru.practicum.gateway.dto.user.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient client;
    private static final String ALTER_USER_PATH = "/{id}";

    @Autowired
    public UserController(UserClient client) {
        this.client = client;
    }

    @PostMapping
    public Mono<User> addUser(@Valid @RequestBody User user) {
        return client.addUser(user);
    }

    @PatchMapping(value = ALTER_USER_PATH)
    public Mono<User> updateUser(@PathVariable("id") Long userId, @Valid @RequestBody User user) {
        return client.updateUser(userId, user);
    }

    @GetMapping(ALTER_USER_PATH)
    public Mono<User> getUserById(@PathVariable("id") Long userId) {
        return client.getUserById(userId);
    }

    @GetMapping
    public Mono<List<User>> getAllUsers() {
        return client.getAllUsers();
    }

    @DeleteMapping(value = ALTER_USER_PATH)
    public Mono<Void> deleteUserById(@PathVariable("id") Long userId) {
        return client.deleteUserById(userId);
    }
}
