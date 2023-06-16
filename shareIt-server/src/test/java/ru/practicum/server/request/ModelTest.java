package ru.practicum.server.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.server.request.model.ItemRequest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

public class ModelTest {
    @Test
    void testEqualsAndHashCode() {
        ItemRequest request1 = new ItemRequest();
        request1.setId(1L);
        ItemRequest request2 = new ItemRequest();
        request2.setId(1L);
        Assertions.assertEquals(request1, request2);
        assertThat(request1.hashCode(), notNullValue());
    }
}
