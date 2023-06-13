package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BookerIsOwnerException;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemUnavailableException;
import ru.practicum.shareit.exception.UpdateStatusAfterApprovalException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.BadUserForItem;
import ru.practicum.shareit.item.exceptions.InvalidItemAvailable;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.exceptions.UserDuplicateException;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.WrongDateException;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
    User ownerTest;
    User bookerTest;
    ItemRequestDto itemRequestDtoTest;
    ItemDto itemDtoTest;
    BookingResponseDto bookingResponseDtoTest;

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
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(requestDto.getId());
        itemRequest.setDescription(requestDto.getDescription());
        return ItemDto.builder()
                .id(Math.toIntExact(requestDto.getId()))
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

    @BeforeEach
    void setUp() throws UserDuplicateException, InvalidItemAvailable {
        User owner = setUser("Test owner", "owner@mail.com");
        ownerTest = userService.addUser(owner);
        User booker = setUser("Test booker", "booker@mail.com");
        bookerTest = userService.addUser(booker);
        ItemRequestDto requestDto = setRequestDto();
        itemRequestDtoTest = requestService.addItemRequest(bookerTest.getId(), requestDto);
        ItemDto itemDto = setItemDto(itemRequestDtoTest);
        itemDtoTest = itemService.addItem(itemDto, ownerTest.getId());
        BookingRequestDto bookingDto = setFutureBookingDto(itemDtoTest);
        bookingResponseDtoTest = bookingService.addBooking(bookerTest.getId(), bookingDto);
    }

    @Test
    void testGetBookingById() {
        assertThat(bookingService.getBookingById(ownerTest.getId(), bookingResponseDtoTest.getId()).getStatus(),
                equalTo(BookingStatus.WAITING));
        Assertions.assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBookingById(ownerTest.getId(), 99L));
    }

    @Test
    void testRejectedStatus() {
        bookingService.setBookingStatus(ownerTest.getId(), bookingResponseDtoTest.getId(), false);
        assertThat(bookingService.getBookingById(ownerTest.getId(), itemRequestDtoTest.getId()).getStatus(),
                equalTo(BookingStatus.REJECTED));
        Assertions.assertThrows(UpdateStatusAfterApprovalException.class,
                () -> bookingService.setBookingStatus(ownerTest.getId(), bookingResponseDtoTest.getId(), true));
    }

    @Test
    void testAddBookingByOwnerFailed() {
        Assertions.assertThrows(BookerIsOwnerException.class,
                () -> bookingService.addBooking(ownerTest.getId(), setFutureBookingDto(itemDtoTest)));
    }

    @Test
    void testAddBookingWithIncorrectDate() {
        BookingRequestDto bookingRequestDto = setFutureBookingDto(itemDtoTest);
        bookingRequestDto.setStart(LocalDateTime.now());
        bookingRequestDto.setEnd(LocalDateTime.now());
        Assertions.assertThrows(WrongDateException.class,
                () -> bookingService.addBooking(bookerTest.getId(), bookingRequestDto));
    }

    @Test
    void testAddBookingUnavailableItemFailed() throws BadUserForItem {
        ItemDto itemDto = itemDtoTest;
        itemDto.setAvailable(false);
        itemService.updateItem(ownerTest.getId(), itemDto, ownerTest.getId());
        Assertions.assertThrows(ItemUnavailableException.class,
                () -> bookingService.addBooking(bookerTest.getId(), setFutureBookingDto(itemDto)));
    }
}
