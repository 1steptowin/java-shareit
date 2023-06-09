package ru.practicum.shareit.request.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.projection.ItemRequestWithItems;

import java.util.List;

@Repository
public interface RequestRepo extends JpaRepository<ItemRequest, Long> {
    List<ItemRequestWithItems> findAllByUser_Id(int ownerId);

    Page<ItemRequestWithItems> findAllByUser_IdNot(int userId, Pageable page);

    ItemRequestWithItems findAllById(Long requestId);
}
