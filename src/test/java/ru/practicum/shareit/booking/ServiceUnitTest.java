package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.projection.BookingShort;
import ru.practicum.shareit.booking.repo.BookingRepo;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.WrongStatusException;
import ru.practicum.shareit.item.projection.ItemShort;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.projection.UserShort;
import ru.practicum.shareit.user.repo.UserRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ServiceUnitTest {
    @Mock
    BookingRepo bookingRepoMock;
    @Mock
    UserRepo userRepoMock;
    @InjectMocks
    BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        User user = setUser(1, "Test user", "testuser@mail.com");
        Mockito.when(userRepoMock.findById(Mockito.anyInt())).thenReturn(Optional.of(user));

        LocalDateTime past = LocalDateTime.now().minusYears(1);
        LocalDateTime future = LocalDateTime.now().plusYears(1);
        LocalDateTime now = LocalDateTime.now();

        BookingShort bookingPast = setBooking(1L, past);
        BookingShort bookingFuture = setBooking(2L, future);
        BookingShort bookingNow = setBooking(3L, now);

        Page<BookingShort> page = new PageImpl<>(List.of(bookingPast, bookingFuture, bookingNow));

        Mockito.lenient().when(bookingRepoMock.findAllByBookerIdOrderByStartDesc(Mockito.anyInt(), Mockito.any()))
                .thenReturn(page);
        Mockito.lenient().when(bookingRepoMock.findAllByBookerIdAndStatusOrderByStartDesc(Mockito.anyInt(),
                Mockito.any(), Mockito.any())).thenReturn(page);
        Mockito.lenient().when(bookingRepoMock.findAllByOwnerIdOrderByStartDesc(Mockito.anyInt(), Mockito.any()))
                .thenReturn(page);
        Mockito.lenient().when(bookingRepoMock.findAllByOwnerIdAndStatusOrderByStartDesc(Mockito.anyInt(),
                Mockito.any(), Mockito.any())).thenReturn(page);
    }

    @Test
    void testGetAllBookingsOfBookerPast() {
        List<BookingResponseDto> bookingsInPast = bookingService
                .getAllBookingsOfBookerByState(1, "PAST", 0, 10);
        assertThat(bookingsInPast, hasSize(1));
        assertThat(bookingsInPast.get(0).getId(), equalTo(1L));
    }

    @Test
    void testGetAllBookingsOfBookerFuture() {
        List<BookingResponseDto> bookingsInFuture = bookingService
                .getAllBookingsOfBookerByState(1, "FUTURE", 0, 10);
        assertThat(bookingsInFuture, hasSize(1));
        assertThat(bookingsInFuture.get(0).getId(), equalTo(2L));
    }

    @Test
    void testGetAllBookingsOfBookerCurrent() throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);
        List<BookingResponseDto> bookingsCurrent = bookingService
                .getAllBookingsOfBookerByState(1, "CURRENT", 0, 10);
        assertThat(bookingsCurrent, hasSize(1));
        assertThat(bookingsCurrent.get(0).getId(), equalTo(3L));
    }

    @Test
    void testGetAllBookingsOfBooker() {
        List<BookingResponseDto> bookingsAll = bookingService
                .getAllBookingsOfBookerByState(1, "ALL", 0, 10);
        assertThat(bookingsAll, hasSize(3));
        assertThat(bookingsAll.get(0).getId(), equalTo(1L));
        assertThat(bookingsAll.get(1).getId(), equalTo(2L));
        assertThat(bookingsAll.get(2).getId(), equalTo(3L));
    }

    @Test
    void testGetAllBookingsOfBookerApproved() {
        List<BookingResponseDto> bookingsApproved = bookingService
                .getAllBookingsOfBookerByState(1, "APPROVED", 0, 10);
        assertThat(bookingsApproved, hasSize(3));
    }

    @Test
    void testGetAllBookingsOfOwnerPast() {
        List<BookingResponseDto> bookingsInPast = bookingService
                .getAllBookingsOfOwnerByState(1, "PAST", 0, 10);
        assertThat(bookingsInPast, hasSize(1));
        assertThat(bookingsInPast.get(0).getId(), equalTo(1L));
    }

    @Test
    void testGetAllBookingsOfOwnerFuture() {
        List<BookingResponseDto> bookingsInFuture = bookingService
                .getAllBookingsOfOwnerByState(1, "FUTURE", 0, 10);
        assertThat(bookingsInFuture, hasSize(1));
        assertThat(bookingsInFuture.get(0).getId(), equalTo(2L));
    }

    @Test
    void testGetAllBookingsOfOwnerCurrent() throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);
        List<BookingResponseDto> bookingsCurrent = bookingService
                .getAllBookingsOfOwnerByState(1, "CURRENT", 0, 10);
        assertThat(bookingsCurrent, hasSize(1));
        assertThat(bookingsCurrent.get(0).getId(), equalTo(3L));
    }

    @Test
    void testGetAllBookingsOfOwner() {
        List<BookingResponseDto> bookingsAll = bookingService
                .getAllBookingsOfOwnerByState(1, "ALL", 0, 10);
        assertThat(bookingsAll, hasSize(3));
    }

    @Test
    void testGetAllBookingsOfOwnerApproved() {
        List<BookingResponseDto> bookingsApproved = bookingService
                .getAllBookingsOfOwnerByState(1, "APPROVED", 0, 10);
        assertThat(bookingsApproved, hasSize(3));
    }

    @Test
    void testGetAllBookingsWrongStatusFail() {
        Assertions.assertThrows(WrongStatusException.class,
                () -> bookingService.getAllBookingsOfBookerByState(1, "UNKNOWN", 0, 10));
    }

    private User setUser(int id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private BookingShort setBooking(Long id, LocalDateTime start) {
        return new BookingShort(id, start, start.plusDays(1),
                BookingStatus.APPROVED, new UserShort(1), new ItemShort(1, "Item"));
    }
}
