package ru.practicum.gateway.dto.item;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.gateway.dto.booking.BookingShortForItem;
import ru.practicum.gateway.dto.comments.CommentWithAuthorName;

import java.util.List;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemWithLastAndNextBookingAndComments {
    Long id;
    Long ownerId;
    String name;
    String description;
    Boolean available;
    BookingShortForItem lastBooking;
    BookingShortForItem nextBooking;
    List<CommentWithAuthorName> comments;
}
