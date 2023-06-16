package ru.practicum.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.practicum.gateway.client.BookingClient;
import ru.practicum.gateway.dto.booking.BookingRequestDto;
import ru.practicum.gateway.dto.booking.BookingResponseDto;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingClient client;
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private static final String BOOKING_PATH = "/{bookingId}";

    @Autowired
    public BookingController(BookingClient client) {
        this.client = client;
    }

    @PostMapping
    public Mono<BookingResponseDto> addBooking(@RequestHeader(USER_HEADER) Long userId,
                                               @RequestBody BookingRequestDto bookingRequestDto) {
        return client.addBooking(userId, bookingRequestDto);
    }

    @PatchMapping(value = BOOKING_PATH)
    public Mono<BookingResponseDto> setBookingStatus(@RequestHeader(USER_HEADER) Long userId,
                                                     @PathVariable("bookingId") Long id, @RequestParam Boolean approved) {
        return client.setBookingStatus(userId, id, approved);
    }

    @GetMapping(BOOKING_PATH)
    public Mono<BookingResponseDto> getBookingById(@RequestHeader(USER_HEADER) Long userId,
                                                   @PathVariable("bookingId") Long id) {
        return client.getBookingById(userId, id);
    }

    @GetMapping
    public Mono<List<BookingResponseDto>> getAllBookingsOfBookerByState(@RequestHeader(USER_HEADER) Long bookerId,
                                                                        @RequestParam(defaultValue = "ALL", required = false) String state,
                                                                        @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                                        @RequestParam(required = false, defaultValue = "10") @PositiveOrZero int size) {
        return client.getAllBookingsOfBookerByState(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public Mono<List<BookingResponseDto>> getAllBookingsOfOwnerByState(@RequestHeader(USER_HEADER) Long ownerId,
                                                                       @RequestParam(defaultValue = "ALL", required = false) String state,
                                                                       @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                                       @RequestParam(required = false, defaultValue = "10") @PositiveOrZero int size) {
        return client.getAllBookingsOfOwnerByState(ownerId, state, from, size);
    }
}
