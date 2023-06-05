package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.projection.BookingShort;
import ru.practicum.shareit.item.projection.ItemShort;
import ru.practicum.shareit.user.projection.UserShort;

public class BookingMapper {
    public static Booking mapDtoToModel(BookingRequestDto bookingRequestDto) {
        Booking booking = new Booking();
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());
        return booking;
    }

    public static BookingResponseDto mapModelToDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(new UserShort(booking.getBooker().getId()))
                .item(new ItemShort(booking.getItem().getId(), booking.getItem().getName()))
                .build();
    }

    public static BookingResponseDto mapProjectionToDto(BookingShort bookingShort) {
        return BookingResponseDto.builder()
                .id(bookingShort.getId())
                .start(bookingShort.getStart())
                .end(bookingShort.getEnd())
                .status(bookingShort.getStatus())
                .booker(bookingShort.getBooker())
                .item(bookingShort.getItem())
                .build();
    }
}
