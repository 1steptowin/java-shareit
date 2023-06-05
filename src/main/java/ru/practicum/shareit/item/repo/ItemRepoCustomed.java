package ru.practicum.shareit.item.repo;
import ru.practicum.shareit.item.projection.ItemWithLastAndNextBookingAndComments;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepoCustomed {

    ItemWithLastAndNextBookingAndComments findItemWithLastAndNextBookingAndComments(int itemId, LocalDateTime now,
                                                                                    boolean isOwner);

    List<ItemWithLastAndNextBookingAndComments> findAllWithLastAndNextBookingAndComments(int userId,
                                                                                         LocalDateTime now);
}
