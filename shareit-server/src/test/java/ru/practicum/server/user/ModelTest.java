package ru.practicum.server.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.server.user.model.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class ModelTest {
    @Test
    void testEqualsAndHashCode() {
        User user1 = new User();
        user1.setId(1);
        User user2 = new User();
        user2.setId(1);
        Assertions.assertEquals(user1, user2);
        assertThat(user1.hashCode(), notNullValue());
    }
}
