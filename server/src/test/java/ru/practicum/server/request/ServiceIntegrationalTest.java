package ru.practicum.server.request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.server.exception.RequestNotFoundException;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.service.ItemService;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.service.RequestService;
import ru.practicum.server.user.model.User;
import ru.practicum.server.exception.UserNotFoundException;
import ru.practicum.server.user.service.UserService;

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
    RequestService requestService;
    @Autowired
    UserService userService;
    @Autowired
    ItemService itemService;
    User addedRequester;
    User addedOwner;
    ItemRequestDto addedRequest;

    private User setUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private ItemRequestDto setRequest() {
        return ItemRequestDto.builder()
                .description("Request")
                .build();
    }

    private ItemDto setItem(ItemRequestDto request) {
        return ItemDto.builder()
                .name("Item test")
                .description("Description test")
                .available(true)
                .requestId(request.getId())
                .build();
    }

    @BeforeEach
    void setUp() {
        addedRequester = userService.addUser(setUser("Requester", "requester@mail.com"));
        addedRequest = requestService.addItemRequest(addedRequester.getId(), setRequest());
        addedOwner = userService.addUser(setUser("Owner", "owner@mail.com"));
        itemService.addItem(setItem(addedRequest), addedOwner.getId());
    }

    @Test
    void testAddRequest() {
        assertThat(requestService.getUsersRequestsWithItems(addedRequester.getId()), hasSize(1));
    }

    @Test
    void testGetUsersRequestsWithItems() {
        int requesterId = addedRequester.getId();
        assertThat(requestService.getUsersRequestsWithItems(requesterId), hasSize(1));
        assertThat(requestService.getUsersRequestsWithItems(requesterId).get(0).getItems(), hasSize(1));
        assertThat(requestService.getUsersRequestsWithItems(requesterId).get(0).getDescription(),
                equalTo("Request"));
        Assertions.assertThrows(UserNotFoundException.class, () -> requestService.getUsersRequestsWithItems(99));

    }

    @Test
    void testGetRequestByIdWithItems() {
        Long requestId = addedRequest.getId();
        int requesterId = addedRequester.getId();
        assertThat(requestService.getRequestByIdWithItems(requesterId, requestId).getItems(), hasSize(1));
        assertThat(requestService.getRequestByIdWithItems(requesterId, requestId).getItems().get(0).getName(),
                equalTo("Item test"));
        Assertions.assertThrows(RequestNotFoundException.class,
                () -> requestService.getRequestByIdWithItems(requesterId, 99L));
    }

    @Test
    void testGetAllRequestsOfOtherUsers() {
        int ownerId = addedOwner.getId();
        assertThat(requestService.getAllRequestsOfOtherUsers(ownerId, 0, 10), hasSize(1));
        Assertions.assertThrows(UserNotFoundException.class,
                () -> requestService.getAllRequestsOfOtherUsers(99, 0, 10));
    }
}
