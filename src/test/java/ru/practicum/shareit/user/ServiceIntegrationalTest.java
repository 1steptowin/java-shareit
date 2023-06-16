package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.EmptyEmailException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.exception.UserNotFoundException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceIntegrationalTest {
    @Autowired
    UserService userService;
    User addedUser;

    private User setUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    @BeforeEach
    void setUp() {
        addedUser = userService.addUser(setUser("user", "user@mail.com"));
    }

    @Test
    void testAddUser() {
        assertThat(userService.getAllUsers(), hasSize(1));
    }

    @Test
    void testAddUserDuplicativeEmail() {
        User user = setUser("user", "user@mail.com");
        Assertions.assertThrows(DataIntegrityViolationException.class, () -> userService.addUser(user));
        User userWithEmptyEmail = setUser("user", null);
        Assertions.assertThrows(EmptyEmailException.class, () -> userService.addUser(userWithEmptyEmail));
    }

    @Test
    void testUpdateUser() {
        User newUser = setUser("updated", "update@mail.com");
        newUser.setId(1);
        User updatedUser = userService.updateUser(newUser);
        assertThat(updatedUser.getName(), equalTo("updated"));
        assertThat(updatedUser.getEmail(), equalTo("update@mail.com"));
    }

    @Test
    void testUpdateUserWithSameEmail() {
        User newUser = setUser("user", "user@mail.com");
        newUser.setId(1);
        User updatedUser = userService.updateUser(newUser);
        assertThat(updatedUser.getName(), equalTo("user"));
        assertThat(updatedUser.getEmail(), equalTo("user@mail.com"));
    }

    @Test
    void testGetUserById() {
        assertThat(addedUser.getName(), equalTo(userService.getUserById(addedUser.getId()).getName()));
        Assertions.assertThrows(UserNotFoundException.class, () -> userService.getUserById(99));
    }

    @Test
    void testGetAllUsers() {
        assertThat(userService.getAllUsers(), hasSize(1));
    }

    @Test
    void deleteUserById() {
        userService.deleteUserById(addedUser.getId());
        assertThat(userService.getAllUsers(), hasSize(0));
    }
}
