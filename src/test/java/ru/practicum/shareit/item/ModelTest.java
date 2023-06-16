package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class ModelTest {
    @Test
    void testEqualAndHashCode() {
        Item item1 = new Item();
        item1.setId(1);
        Item item2 = new Item();
        item2.setId(1);
        Assertions.assertEquals(item1, item2);
        assertThat(item1.hashCode(), notNullValue());
    }
}
