package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.BadUserForItem;
import ru.practicum.shareit.item.exceptions.InvalidItemAvailable;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.TextIsBlank;
import ru.practicum.shareit.item.projection.ItemWithLastAndNextBookingAndComments;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exceptions.UserDuplicateException;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceIntegrationalTest {
    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;
    @Autowired
    RequestService requestService;
    @Autowired
    BookingService bookingService;
    ItemDto addedItem;
    User addedOwner;
    User addedBooker;
    BookingResponseDto approvedBooking;
    ItemRequestDto addedRequest;

    @BeforeEach
    void setUp() throws UserDuplicateException, InvalidItemAvailable {
        User owner = setUser("Owner", "owner@mail.com");
        addedOwner = userService.addUser(owner);
        User booker = setUser("Booker", "booker@mail.com");
        addedBooker = userService.addUser(booker);
        ItemRequestDto requestDto = setRequestDto();
        addedRequest = requestService.addItemRequest(addedBooker.getId(), requestDto);
        ItemDto itemDto = setItemDto(addedRequest);
        addedItem = itemService.addItem(itemDto, addedOwner.getId());
        BookingRequestDto bookingDto = setFutureBookingDto(addedItem);
        BookingResponseDto addedBooking = bookingService.addBooking(addedBooker.getId(), bookingDto);
        approvedBooking = bookingService.setBookingStatus(addedOwner.getId(), addedBooking.getId(), true);
    }

    @Test
    void testAddItemFail() {
        ItemDto itemDtoWithEmptyAvailable = setItemDto(addedRequest);
        itemDtoWithEmptyAvailable.setAvailable(null);
        Assertions.assertThrows(EmptyItemAvailabilityException.class,
                () -> itemService.addItem(itemDtoWithEmptyAvailable, addedOwner.getId()));
        ItemDto itemDtoWithEmptyName = setItemDto(addedRequest);
        itemDtoWithEmptyName.setName(null);
        Assertions.assertThrows(EmptyItemNameException.class,
                () -> itemService.addItem(itemDtoWithEmptyName, addedOwner.getId()));
        ItemDto itemDtoWithEmptyDescription = setItemDto(addedRequest);
        itemDtoWithEmptyDescription.setDescription(null);
        Assertions.assertThrows(EmptyItemDescriptionException.class,
                () -> itemService.addItem(itemDtoWithEmptyDescription, addedOwner.getId()));
    }

    @Test
    void testUpdateItem() throws BadUserForItem {
        ItemDto updatedItem = addedItem;
        updatedItem.setAvailable(false);
        updatedItem.setName("Updated");
        updatedItem.setDescription("Updated");
        assertThat(itemService.updateItem(updatedItem.getId(), updatedItem, addedOwner.getId()).getName(),
                equalTo(addedItem.getName()));
        Assertions.assertThrows(NonOwnerUpdatingException.class,
                () -> itemService.updateItem(updatedItem.getId(), updatedItem, 3));
        Assertions.assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(2, updatedItem, addedOwner.getId()));
    }

    @Test
    void testSearch() throws TextIsBlank {
        assertThat(itemService.search("Item"), hasSize(1));
    }

    @Test
    void testSearchEmpty() throws TextIsBlank {
        assertThat(itemService.search(""), hasSize(0));
    }

    @Test
    void testAddComment() throws InterruptedException {
        BookingRequestDto current = setCurrentBookingDto(addedItem);
        BookingResponseDto addedCurrent = bookingService.addBooking(addedBooker.getId(), current);
        bookingService.setBookingStatus(addedOwner.getId(), addedCurrent.getId(), true);
        CommentDto comment = setComment();
        Assertions.assertThrows(IllegalCommentException.class,
                () -> itemService.addComment(addedBooker.getId(), addedItem.getId(), comment));
        TimeUnit.SECONDS.sleep(5);
        itemService.addComment(addedBooker.getId(), addedItem.getId(), comment);
        Assertions.assertThrows(ItemNotFoundException.class,
                () -> itemService.addComment(addedBooker.getId(), 99, comment));
        assertThat(itemService.getItem(addedOwner.getId(), addedItem.getId()).getComments(), hasSize(1));
    }

    @Test
    void testFindItems() {
        ItemWithLastAndNextBookingAndComments itemFull = itemService.getItem(addedOwner.getId(), addedItem.getId());
        assertThat(itemFull.getNextBooking().getId(), equalTo(approvedBooking.getId()));
        assertThat(itemFull.getNextBooking().getBookerId(), equalTo(approvedBooking.getBooker().getId()));
        assertNull(itemFull.getLastBooking());
        assertThat(itemFull.getComments(), hasSize(0));
        List<ItemWithLastAndNextBookingAndComments> itemFullAll = itemService.getItems(addedOwner.getId());
        assertThat(itemFullAll, hasSize(1));
        assertThat(itemFullAll.get(0).getNextBooking().getId(), equalTo(approvedBooking.getId()));
        assertThat(itemFullAll.get(0).getNextBooking().getBookerId(), equalTo(approvedBooking.getBooker().getId()));
        assertNull(itemFullAll.get(0).getLastBooking());
        assertThat(itemFullAll.get(0).getComments(), hasSize(0));
    }

    private User setUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private ItemRequestDto setRequestDto() {
        return ItemRequestDto.builder()
                .description("Request")
                .build();
    }

    private ItemDto setItemDto(ItemRequestDto requestDto) {
        ItemRequest itemRequest = ItemRequestMapper.mapDtoToModel(requestDto);
        itemRequest.setId(requestDto.getId());
        return ItemDto.builder()
                .name("Item name")
                .description("Item description")
                .available(true)
                .requestId(itemRequest.getId())
                .build();
    }

    private BookingRequestDto setFutureBookingDto(ItemDto itemDto) {
        BookingRequestDto bookingDto = new BookingRequestDto();
        LocalDateTime nextStart = LocalDateTime.now().plusDays(1);
        LocalDateTime nextEnd = nextStart.plusDays(2);
        bookingDto.setStart(nextStart);
        bookingDto.setEnd(nextEnd);
        bookingDto.setItemId(itemDto.getId());
        return bookingDto;
    }

    private BookingRequestDto setCurrentBookingDto(ItemDto itemDto) {
        BookingRequestDto bookingDto = new BookingRequestDto();
        LocalDateTime currentStart = LocalDateTime.now().plusSeconds(2);
        LocalDateTime currentEnd = currentStart.plusDays(1);
        bookingDto.setStart(currentStart);
        bookingDto.setEnd(currentEnd);
        bookingDto.setItemId(itemDto.getId());
        return bookingDto;
    }

    private CommentDto setComment() {
        CommentDto comment = new CommentDto();
        comment.setText("Comment");
        return comment;
    }
}
