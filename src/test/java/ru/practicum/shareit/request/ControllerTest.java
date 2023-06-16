package ru.practicum.shareit.request;

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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.service.RequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    RequestService requestService;
    @Autowired
    MockMvc mvc;
    ItemRequestDto request;
    ItemRequestWithItemsDto requestWithItems;
    ItemDto item;
    final LocalDateTime testNow = LocalDateTime.now();
    static final String userIdHeader = "X-Sharer-User-Id";

    private ItemDto setItemDto() {
        return ItemDto.builder()
                .id(1)
                .name("Item")
                .description("New item description")
                .available(true)
                .requestId(1L)
                .build();
    }

    private ItemRequestDto setItemRequestDto() {
        return ItemRequestDto.builder()
                .id(1L)
                .description("New item")
                .created(testNow)
                .build();
    }

    private ItemRequestWithItemsDto setItemRequestWithItems() {
        return ItemRequestWithItemsDto.builder()
                .id(1L)
                .description("Request with items")
                .created(testNow)
                .items(List.of(item))
                .build();
    }

    @BeforeEach
    void setUp() {
        item = setItemDto();
        request = setItemRequestDto();
        requestWithItems = setItemRequestWithItems();
    }

    @Test
    void testAddRequest() throws Exception {
        Mockito.when(requestService.addItemRequest(Mockito.anyInt(), Mockito.any())).thenReturn(request);
        mvc.perform(post("/requests")
                        .header(userIdHeader, "1")
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())));
    }

    @Test
    void testGetUsersRequestWithItems() throws Exception {
        Mockito.when(requestService.getUsersRequestsWithItems(Mockito.anyInt())).thenReturn(List.of(requestWithItems));
        mvc.perform(get("/requests")
                        .header(userIdHeader, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestWithItems.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestWithItems.getDescription())))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$[0].items[0].requestId", is(requestWithItems.getId()), Long.class));
    }

    @Test
    void testGetRequestByIdWithItems() throws Exception {
        Mockito.when(requestService.getRequestByIdWithItems(Mockito.anyInt(), Mockito.anyLong()))
                .thenReturn(requestWithItems);
        mvc.perform(get("/requests/1")
                        .header(userIdHeader, "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(requestWithItems.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(requestWithItems.getDescription())))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$.items[0].requestId", is(requestWithItems.getId()), Long.class));
    }

    @Test
    void testGetAllRequestsOfOtherUsers() throws Exception {
        Mockito.when(requestService.getAllRequestsOfOtherUsers(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()))
                .thenReturn(List.of(requestWithItems));
        mvc.perform(get("/requests/all")
                        .header(userIdHeader, "1")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(requestWithItems.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestWithItems.getDescription())))
                .andExpect(jsonPath("$[0].items", hasSize(1)))
                .andExpect(jsonPath("$[0].items[0].id", is(item.getId()), Integer.class))
                .andExpect(jsonPath("$[0].items[0].requestId", is(requestWithItems.getId()), Long.class));
    }

}
