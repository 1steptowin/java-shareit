package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.projection.BookingShortForItem;
import ru.practicum.shareit.exception.EmptyItemAvailabilityException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.projection.CommentWithAuthorName;
import ru.practicum.shareit.item.projection.ItemWithLastAndNextBookingAndComments;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @Autowired
    MockMvc mvc;
    final String userIdHeader = "X-Sharer-User-Id";
    ItemDto item;
    CommentWithAuthorName comment;
    ItemWithLastAndNextBookingAndComments itemWithInfo;
    final LocalDateTime testNow = LocalDateTime.now();

    private ItemDto setItemDto() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        return ItemDto.builder()
                .id(1)
                .name("New item")
                .description("Item description")
                .available(true)
                .requestId(itemRequest.getId())
                .build();
    }

    private ItemWithLastAndNextBookingAndComments setItemFull() {
        return ItemWithLastAndNextBookingAndComments.builder()
                .id(1)
                .owner(1)
                .name("New item")
                .description("Item description")
                .available(true)
                .nextBooking(new BookingShortForItem(1L, 1))
                .lastBooking(new BookingShortForItem(2L, 1))
                .comments(List.of(comment))
                .build();
    }

    @BeforeEach
    void setUp() {
        item = setItemDto();
        comment = new CommentWithAuthorName(1L, "Comment", "Author", testNow);
        itemWithInfo = setItemFull();
    }

    @Test
    void testAddItem() throws Exception {
        Mockito.when(itemService.addItem(Mockito.any(), Mockito.anyInt())).thenReturn(item);
        mvc.perform(post("/items")
                        .header(userIdHeader, "1")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(item.getRequestId().intValue())));
    }

    @Test
    void testAddItemEmptyAvailabilityFail() throws Exception {
        Mockito.when(itemService.addItem(Mockito.any(), Mockito.anyInt()))
                .thenThrow(new EmptyItemAvailabilityException("Empty available"));
        mvc.perform(post("/items")
                        .header(userIdHeader, "1")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", notNullValue()));
    }

    @Test
    void testAddComment() throws Exception {
        Mockito.when(itemService.addComment(Mockito.anyInt(), Mockito.anyInt(), Mockito.any())).thenReturn(comment);
        mvc.perform(post("/items/1/comment")
                        .header(userIdHeader, "1")
                        .content(mapper.writeValueAsString(comment))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())));
    }

    @Test
    void testUpdateItem() throws Exception {
        item.setDescription("Updated description");
        Mockito.when(itemService.updateItem(Mockito.anyInt(), Mockito.any(), Mockito.anyInt())).thenReturn(item);
        mvc.perform(patch("/items/1")
                        .header(userIdHeader, "1")
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(item.getRequestId().intValue())));
    }

    @Test
    void testGetItemById() throws Exception {
        Mockito.when(itemService.getItem(Mockito.anyInt(), Mockito.anyInt())).thenReturn(itemWithInfo);
        mvc.perform(get("/items/1")
                        .header(userIdHeader, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemWithInfo.getId()), Integer.class))
                .andExpect(jsonPath("$.name", is(itemWithInfo.getName())))
                .andExpect(jsonPath("$.description", is(itemWithInfo.getDescription())))
                .andExpect(jsonPath("$.available", is(itemWithInfo.getAvailable())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemWithInfo.getLastBooking().getId().intValue())))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(itemWithInfo.getLastBooking().getBookerId())))
                .andExpect(jsonPath("$.nextBooking.id", is(itemWithInfo.getNextBooking().getId().intValue())))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(itemWithInfo.getNextBooking().getBookerId())))
                .andExpect(jsonPath("$.comments", hasSize(1)))
                .andExpect(jsonPath("$.comments[0].id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.comments[0].text", is(comment.getText())))
                .andExpect(jsonPath("$.comments[0].authorName", is(comment.getAuthorName())));
    }

    @Test
    void testGetItemByIdNotFoundFail() throws Exception {
        Mockito.when(itemService.getItem(Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new ItemNotFoundException("Item not found"));
        mvc.perform(get("/items/1")
                        .header(userIdHeader, "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", notNullValue()));
    }

    @Test
    void testGetOwnersItems() throws Exception {
        Mockito.when(itemService.getItems(Mockito.anyInt(),Mockito.anyInt(),Mockito.anyInt())).thenReturn(List.of(itemWithInfo));
        mvc.perform(get("/items")
                        .header(userIdHeader, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemWithInfo.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemWithInfo.getName())))
                .andExpect(jsonPath("$[0].description", is(itemWithInfo.getDescription())))
                .andExpect(jsonPath("$[0].available", is(itemWithInfo.getAvailable())))
                .andExpect(jsonPath("$[0].lastBooking.id", is(itemWithInfo.getLastBooking().getId().intValue())))
                .andExpect(jsonPath("$[0].lastBooking.bookerId", is(itemWithInfo.getLastBooking().getBookerId())))
                .andExpect(jsonPath("$[0].nextBooking.id", is(itemWithInfo.getNextBooking().getId().intValue())))
                .andExpect(jsonPath("$[0].nextBooking.bookerId", is(itemWithInfo.getNextBooking().getBookerId())))
                .andExpect(jsonPath("$[0].comments", hasSize(1)))
                .andExpect(jsonPath("$[0].comments[0].id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$[0].comments[0].text", is(comment.getText())))
                .andExpect(jsonPath("$[0].comments[0].authorName", is(comment.getAuthorName())));
    }

    @Test
    void testSearchItems() throws Exception {
        Mockito.when(itemService.search(Mockito.anyString(), Mockito.anyInt(),Mockito.anyInt())).thenReturn(List.of(item));
        mvc.perform(get("/items/search")
                        .param("text", "item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item.getAvailable())))
                .andExpect(jsonPath("$[0].requestId", is(item.getRequestId().intValue())));
    }
}
