package ru.practicum.server.booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.projection.BookingShort;
import ru.practicum.server.booking.repo.BookingRepo;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repo.ItemRepo;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.repo.RequestRepo;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repo.UserRepo;

import java.time.LocalDateTime;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RepoTest {
    @Autowired
    BookingRepo bookingRepo;
    @Autowired
    TestEntityManager tem;
    @Autowired
    ItemRepo itemRepo;
    @Autowired
    UserRepo userRepo;
    @Autowired
    RequestRepo requestRepo;
    Booking booking;
    User booker;
    User owner;
    Item item;
    final Pageable request = PageRequest.of(0, 10);

    private User setUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return user;
    }

    private ItemRequest setRequest(User user) {
        ItemRequest request = new ItemRequest();
        request.setDescription("Request for test item");
        request.setUser(user);
        return request;
    }

    private Item setItem(User user, ItemRequest request) {
        Item item = new Item();
        item.setName("Test item");
        item.setDescription("Test item description");
        item.setOwner(user.getId());
        item.setAvailable(true);
        item.setRequest(request);
        item.setComments(null);
        return item;
    }

    private Booking setBooking(User user, Item item) {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusDays(1));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        return booking;
    }

    @BeforeEach
    void setUp() {
        owner = setUser("Test user", "user@mail.com");
        booker = setUser("Test owner", "owner@mail.com");
        userRepo.save(owner);
        userRepo.save(booker);
        ItemRequest request = setRequest(owner);
        requestRepo.save(request);
        item = setItem(owner, request);
        itemRepo.save(item);
        booking = setBooking(booker, item);
        bookingRepo.save(booking);
    }

    @Test
    void testUpdateStatus() {
        bookingRepo.updateStatus(booking.getId(), BookingStatus.APPROVED);
        Assertions.assertEquals(BookingStatus.APPROVED, bookingRepo.findById(booking.getId()).orElseThrow().getStatus());
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(tem);
    }

    @Test
    void testFindBookingShortByBookingId() {
        BookingShort bookingShort = bookingRepo.findBookingShortByBookingId(booking.getId());
        Assertions.assertEquals(booking.getId(), bookingShort.getId());
        Assertions.assertEquals(booking.getStatus(), bookingShort.getStatus());
        Assertions.assertEquals(booker.getId(), bookingShort.getBooker().getId());
        Assertions.assertEquals(item.getId(), bookingShort.getItem().getId());
    }

    @Test
    void testFindAllByBookerIdOrderByStartDesc() {
        Page<BookingShort> bookings = bookingRepo.findAllByBookerIdOrderByStartDesc(booker.getId(), request);
        Assertions.assertEquals(1, bookings.getContent().size());
        Assertions.assertEquals(booking.getId(), bookings.getContent().get(0).getId());
        Assertions.assertEquals(booking.getStatus(), bookings.getContent().get(0).getStatus());
        Assertions.assertEquals(booker.getId(), bookings.getContent().get(0).getBooker().getId());
        Assertions.assertEquals(item.getId(), bookings.getContent().get(0).getItem().getId());
    }

    @Test
    void testFindAllByBookerIdAndStatusOrderByStartDesc() {
        Page<BookingShort> bookings = bookingRepo
                .findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.WAITING, request);
        Assertions.assertEquals(1, bookings.getContent().size());
        Assertions.assertEquals(booking.getId(), bookings.getContent().get(0).getId());
        Assertions.assertEquals(booking.getStatus(), bookings.getContent().get(0).getStatus());
        Assertions.assertEquals(booker.getId(), bookings.getContent().get(0).getBooker().getId());
        Assertions.assertEquals(item.getId(), bookings.getContent().get(0).getItem().getId());
    }

    @Test
    void testFindAllByOwnerIdOrderByStartDesc() {
        Page<BookingShort> bookings = bookingRepo
                .findAllByOwnerIdOrderByStartDesc(owner.getId(), request);
        Assertions.assertEquals(1, bookings.getContent().size());
        Assertions.assertEquals(booking.getId(), bookings.getContent().get(0).getId());
        Assertions.assertEquals(booking.getStatus(), bookings.getContent().get(0).getStatus());
        Assertions.assertEquals(booker.getId(), bookings.getContent().get(0).getBooker().getId());
        Assertions.assertEquals(item.getId(), bookings.getContent().get(0).getItem().getId());
    }

    @Test
    void testFindAllByOwnerIdAndStatusOrderByStartDesc() {
        Page<BookingShort> bookings = bookingRepo
                .findAllByOwnerIdAndStatusOrderByStartDesc(owner.getId(), BookingStatus.WAITING, request);
        Assertions.assertEquals(1, bookings.getContent().size());
        Assertions.assertEquals(booking.getId(), bookings.getContent().get(0).getId());
        Assertions.assertEquals(booking.getStatus(), bookings.getContent().get(0).getStatus());
        Assertions.assertEquals(booker.getId(), bookings.getContent().get(0).getBooker().getId());
        Assertions.assertEquals(item.getId(), bookings.getContent().get(0).getItem().getId());
    }
}
