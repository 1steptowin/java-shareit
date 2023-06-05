package ru.practicum.shareit.item.projection;
import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.projection.BookingShortForItem;

import java.util.List;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemWithLastAndNextBookingAndComments {
    int id;
    int owner;
    String name;
    String description;
    Boolean available;
    BookingShortForItem lastBooking;
    BookingShortForItem nextBooking;
    List<CommentWithAuthorName> comments;
}
