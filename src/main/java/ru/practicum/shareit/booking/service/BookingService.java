package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    BookingResponseDto addBooking(int userId, BookingRequestDto bookingRequestDto);

    BookingResponseDto setBookingStatus(int userId, Long id, Boolean approved);

    BookingResponseDto getBookingById(int userId, Long id);

    List<BookingResponseDto> getAllBookingsOfBookerByState(int bookerId, String state, int from, int size);

    List<BookingResponseDto> getAllBookingsOfOwnerByState(int ownerId, String state, int from, int size);
}
