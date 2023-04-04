package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.exceptions.UserDuplicateException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
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
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer userId) throws UserNotFoundException {
        return userService.getUserById(userId);
    }

    @PostMapping()
    public User addUser(@Valid @RequestBody User user) throws UserDuplicateException, UserNotFoundException {
        return userService.addUser(user);
    }

    @PatchMapping("/{userId}")
    public User updateUser(@PathVariable("userId") Integer userId, @RequestBody User user) throws UserNotFoundException, UserDuplicateException {
        user.setId(userId);
        return userService.updateUser(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") Integer userId) throws UserNotFoundException {
        userService.deleteUserById(userId);
    }
}
