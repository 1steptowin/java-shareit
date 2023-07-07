package ru.practicum.server.item.repo;
import org.springframework.data.domain.Pageable;
import ru.practicum.server.item.projection.ItemWithLastAndNextBookingAndComments;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepoCustomed {

    ItemWithLastAndNextBookingAndComments findItemWithLastAndNextBookingAndComments(int itemId, LocalDateTime now,
                                                                                    boolean isOwner);

    List<ItemWithLastAndNextBookingAndComments> findAllWithLastAndNextBookingAndComments(int userId,
                                                                                         LocalDateTime now,
                                                                                         Pageable page);
}