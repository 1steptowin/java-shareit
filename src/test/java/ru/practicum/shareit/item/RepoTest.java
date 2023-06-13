package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repo.ItemRepo;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.RequestRepo;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.repo.UserRepo;

@DataJpaTest
@AutoConfigureTestDatabase
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepoTest {
    @Autowired
    TestEntityManager tem;
    @Autowired
    ItemRepo itemRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    RequestRepo requestRepo;
    Item item;

    private User setUser() {
        User user = new User();
        user.setName("Test user");
        user.setEmail("test@mail.com");
        return user;
    }

    private ItemRequest setRequest(User user) {
        ItemRequest request = new ItemRequest();
        request.setDescription("Request for test");
        request.setUser(user);
        return request;
    }

    private Item setItem(User user, ItemRequest request) {
        Item item = new Item();
        item.setName("Test item");
        item.setDescription("New available test");
        item.setOwner(user.getId());
        item.setAvailable(true);
        item.setRequest(request);
        item.setComments(null);
        return item;
    }

    @BeforeEach
    void setUp() {
        User owner = setUser();
        userRepo.save(owner);
        ItemRequest request = setRequest(owner);
        requestRepo.save(request);
        item = setItem(owner, request);
        itemRepo.save(item);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(tem);
    }

    @Test
    void testPersistingItem() {
        Assertions.assertNotNull(item.getId());
    }
    @Test
    void testUpdateAvailable() {
        item.setAvailable(false);
        Assertions.assertFalse(itemRepo.findById(item.getId()).orElseThrow().getAvailable());
    }

    @Test
    void testUpdateDescription() {
        item.setDescription("Updated");
        Assertions.assertEquals("Updated", itemRepo.findById(item.getId())
                .orElseThrow().getDescription());
    }

    @Test
    void testUpdateName() {
        item.setName("Updated");
        Assertions.assertEquals("Updated", itemRepo.findById(item.getId()).orElseThrow().getName());
    }
}
