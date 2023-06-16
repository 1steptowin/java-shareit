package ru.practicum.server.booking.projection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.item.projection.ItemShort;
import ru.practicum.server.user.projection.UserShort;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingShort {
    Long id;
    LocalDateTime start;
    LocalDateTime end;
    BookingStatus status;
    UserShort booker;
    ItemShort item;

    public BookingShort(Long id, LocalDateTime start, LocalDateTime end, BookingStatus status,
                        int userId, int itemId, String itemName) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.status = status;
        this.booker = new UserShort(userId);
        this.item = new ItemShort(itemId, itemName);
    }
}
