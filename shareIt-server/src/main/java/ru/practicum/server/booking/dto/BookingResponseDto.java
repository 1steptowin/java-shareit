package ru.practicum.server.booking.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.item.projection.ItemShort;
import ru.practicum.server.user.projection.UserShort;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BookingResponseDto {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status;
    UserShort booker;
    ItemShort item;
}
