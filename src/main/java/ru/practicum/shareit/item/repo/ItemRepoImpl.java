package ru.practicum.shareit.item.repo;

import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.projection.BookingShortForItem;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.projection.CommentWithAuthorName;
import ru.practicum.shareit.item.projection.ItemWithLastAndNextBookingAndComments;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ItemRepoImpl implements ItemRepoCustomed {
    private final ItemRepo itemRepo;
    private final CommentRepo commentRepo;

    public ItemRepoImpl(@Lazy ItemRepo itemRepo, CommentRepo commentRepo) {
        this.itemRepo = itemRepo;
        this.commentRepo = commentRepo;
    }

    @Override
    public ItemWithLastAndNextBookingAndComments findItemWithLastAndNextBookingAndComments(int itemId,
                                                                                           LocalDateTime now,
                                                                                           boolean isOwner) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> {
                    throw new ItemNotFoundException("Item does not exist");
                });
        List<Booking> bookings = item.getBookings();
        BookingShortForItem lastBooking;
        BookingShortForItem nextBooking;
        if (bookings == null || bookings.isEmpty() || !isOwner) {
            lastBooking = null;
            nextBooking = null;
        } else {
            lastBooking = getLastBooking(bookings, now);
            nextBooking = getNextBooking(bookings, now);
        }
        List<CommentWithAuthorName> comments = getComments(commentRepo.findAllWithAuthorNameByItemId(itemId));
        return ItemWithLastAndNextBookingAndComments.builder()
                .id(item.getId())
                .owner(item.getOwner())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }

    @Override
    public List<ItemWithLastAndNextBookingAndComments> findAllWithLastAndNextBookingAndComments(int owner, LocalDateTime now) {
        List<Item> ownersItems = itemRepo.findAllByOwner(owner);
        return ownersItems.stream().map(item -> findItemWithLastAndNextBookingAndComments(item.getId(), now, true))
                .collect(Collectors.toList());
    }

    private List<CommentWithAuthorName> getComments(List<CommentWithAuthorName> itemComments) {
        List<CommentWithAuthorName> comments;
        if (itemComments.isEmpty()) {
            comments = List.of();
        } else {
            comments = itemComments;
        }
        return comments;
    }

    private BookingShortForItem getLastBooking(List<Booking> bookings, LocalDateTime now) {
        Optional<Booking> lastBookingOpt = bookings.stream()
                .filter(b -> b.getEnd().isBefore(now) ^ (b.getStart().isBefore(now) && b.getEnd().isAfter(now)))
                .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                .max(Comparator.comparing(Booking::getEnd));
        if (lastBookingOpt.isEmpty()) {
            return null;
        } else {
            return new BookingShortForItem(lastBookingOpt.get().getId(), lastBookingOpt.get().getBooker().getId());
        }
    }

    private BookingShortForItem getNextBooking(List<Booking> bookings, LocalDateTime now) {
        Optional<Booking> nextBookingOpt = bookings.stream()
                .filter(b -> b.getStart().isAfter(now))
                .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                .min(Comparator.comparing(Booking::getStart));
        if (nextBookingOpt.isEmpty()) {
            return null;
        } else {
            return new BookingShortForItem(nextBookingOpt.get().getId(), nextBookingOpt.get().getBooker().getId());
        }
    }
}

