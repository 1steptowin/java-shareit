package ru.practicum.server.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repo.UserRepo;

@DataJpaTest
@AutoConfigureTestDatabase
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepoTest {
    @Autowired
    TestEntityManager tem;
    @Autowired
    UserRepo userRepo;
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test name");
        user.setEmail("user@mail.com");
        userRepo.save(user);
    }

    @Test
    public void contextLoads() {
        Assertions.assertNotNull(tem);
    }

    @Test
    void testPersistingUser() {
        Assertions.assertNotNull(user.getId());
    }

    @Test
    void testUpdateUserEmail() {
        userRepo.updateUserEmail(user.getId(), "update@mail.com");
        Assertions.assertEquals("update@mail.com", userRepo.findById(user.getId()).orElseThrow().getEmail());
    }

    @Test
    void testUpdateUserName() {
        userRepo.updateUserName(user.getId(), "New user name");
        Assertions.assertEquals("New user name", userRepo.findById(user.getId()).orElseThrow().getName());
    }
}
