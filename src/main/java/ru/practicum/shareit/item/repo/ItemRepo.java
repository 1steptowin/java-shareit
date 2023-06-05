package ru.practicum.shareit.item.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.projection.ItemWithLastAndNextBookingAndComments;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ItemRepo extends JpaRepository<Item, Integer>, ItemRepoCustomed {
    List<Item> findAllByOwner(int ownerId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Item i set i.available = ?2 where i.id = ?1")
    void updateAvailable(int id, Boolean available);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Item i set i.description = ?2 where i.id = ?1")
    void updateDescription(int id, String description);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update Item i set i.name = ?2 where i.id = ?1")
    void updateName(int id, String name);

    List<Item> findAllByDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(String description, String name);

    ItemWithLastAndNextBookingAndComments findItemWithLastAndNextBookingAndComments(int itemId, LocalDateTime now,
                                                                                    boolean isOwner);
    List<ItemWithLastAndNextBookingAndComments> findAllWithLastAndNextBookingAndComments(int userId,
                                                                                         LocalDateTime now);
}
