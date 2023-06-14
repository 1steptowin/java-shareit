package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.projection.BookingShort;
import ru.practicum.shareit.item.projection.ItemShort;
import ru.practicum.shareit.user.projection.UserShort;
@UtilityClass
public class BookingMapper {
    public Booking mapDtoToModel(BookingRequestDto bookingRequestDto) {
        Booking booking = new Booking();
        booking.setStart(bookingRequestDto.getStart());
        booking.setEnd(bookingRequestDto.getEnd());
        return booking;
    }

    public BookingResponseDto mapModelToDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .booker(new UserShort(booking.getBooker().getId()))
                .item(new ItemShort(booking.getItem().getId(), booking.getItem().getName()))
                .build();
    }

    public BookingResponseDto mapProjectionToDto(BookingShort bookingShort) {
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
