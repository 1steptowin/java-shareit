package ru.practicum.server.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.server.booking.dto.BookingRequestDto;
import ru.practicum.server.booking.dto.BookingResponseDto;
import ru.practicum.server.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;
    private static final String userIdHeader = "X-Sharer-User-Id";
    private static final String BOOKING_PATH = "/{bookingId}";

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponseDto addBooking(@RequestHeader(userIdHeader) int userId,
                                         @RequestBody @Valid BookingRequestDto bookingRequestDto) {
        log.info("Получен POST запрос /bookings");
        return bookingService.addBooking(userId, bookingRequestDto);
    }

    @PatchMapping(value = BOOKING_PATH)
    public BookingResponseDto setBookingStatus(@RequestHeader(userIdHeader) int userId,
                                               @PathVariable("bookingId") Long id, @RequestParam Boolean approved) {
        log.info("Получен PATCH запрос /bookings/{bookingId}");
        return bookingService.setBookingStatus(userId, id, approved);
    }

    @GetMapping(BOOKING_PATH)
    public BookingResponseDto getBookingById(@RequestHeader(userIdHeader) int userId,
                                             @PathVariable("bookingId") Long id) {
        log.info("Получен GET запрос /bookings/{bookingId}");
        return bookingService.getBookingById(userId, id);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookingsOfBookerByState(@RequestHeader(userIdHeader) int bookerId,
                                                                  @RequestParam(defaultValue = "ALL") String state,
                                                                  @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                                  @RequestParam(required = false, defaultValue = "10") @PositiveOrZero int size) {
        log.info("Получен GET запрос /bookings");
        return bookingService.getAllBookingsOfBookerByState(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingsOfOwnerByState(@RequestHeader(userIdHeader) int ownerId,
                                                                 @RequestParam(defaultValue = "ALL", required = false) String state,
                                                                 @RequestParam(required = false, defaultValue = "0") @PositiveOrZero int from,
                                                                 @RequestParam(required = false, defaultValue = "10") @PositiveOrZero int size) {
        log.info("Получен GET запрос /bookings/owner");
        return bookingService.getAllBookingsOfOwnerByState(ownerId, state, from, size);
    }

}

