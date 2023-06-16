package ru.practicum.server.item.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.server.ShareitPageRequest;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.exception.*;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.mapper.CommentMapper;
import ru.practicum.server.item.mapper.ItemMapper;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.projection.CommentWithAuthorName;
import ru.practicum.server.item.projection.ItemWithLastAndNextBookingAndComments;
import ru.practicum.server.item.repo.CommentRepo;
import ru.practicum.server.item.repo.ItemRepo;
import ru.practicum.server.request.repo.RequestRepo;
import ru.practicum.server.user.repo.UserRepo;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {
    ItemRepo itemRepo;
    RequestRepo requestRepo;
    UserRepo userRepo;
    CommentRepo commentRepo;
    Function<LocalDateTime, Predicate<Booking>> nonFutureBookingsFunction = now ->
            b -> !b.getStart().isAfter(now);

    @Autowired
    public ItemServiceImpl(ItemRepo itemRepo, RequestRepo requestRepo, UserRepo userRepo, CommentRepo commentRepo) {
        this.itemRepo = itemRepo;
        this.requestRepo = requestRepo;
        this.userRepo = userRepo;
        this.commentRepo = commentRepo;
    }

    private void checkIfUserIsOwner(int ownerId, int itemId) {
        if (!(itemRepo.findById(itemId).orElseThrow(() -> {
                    throw new ItemNotFoundException("Item " + itemId + " does not exist");
                })
                .getOwner() == ownerId)) {
            throw new NonOwnerUpdatingException("Item can be updated only by its owner");
        }
    }

    private void checkIfUserExists(int userId) {
        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException("User " + userId + " not found");
        }
    }

    private void checkIfCommentRelatedToCurrentBooking(int userId, int itemId, LocalDateTime now) {
        Item item = itemRepo.findById(itemId)
                .orElseThrow(() -> {
                    throw new ItemNotFoundException("Item " + itemId + " does not exist");
                });
        List<Booking> usersBookingsOfItem = item.getBookings().stream()
                .filter(b -> b.getBooker().getId() == (userId))
                .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                .filter(nonFutureBookingsFunction.apply(now))
                .collect(Collectors.toList());
        if (usersBookingsOfItem.isEmpty()) {
            throw new IllegalCommentException("Illegal comment");
        }
    }

    @Transactional
    @Override
    public ItemDto addItem(ItemDto itemDto, int userId) {
        if (itemDto.getAvailable() == null) {
            throw new EmptyItemAvailabilityException("Item availability is empty");
        }
        if (itemDto.getName() == null || itemDto.getName().isEmpty()) {
            throw new EmptyItemNameException("Item name is empty");
        }
        if (itemDto.getDescription() == null) {
            throw new EmptyItemDescriptionException("Item description is empty");
        }
        checkIfUserExists(userId);
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(userId);
        if (itemDto.getRequestId() != null) {
                item.setRequest(requestRepo.findById(itemDto.getRequestId())
                        .orElseThrow(() -> {
                            throw new RequestNotFoundException("Request does not exist");
                        }));

        }
        return ItemMapper.toItemDto(itemRepo.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(int itemID, ItemDto itemDto, int userId) {
        checkIfUserIsOwner(userId,itemID);
        Item item = ItemMapper.toItem(itemDto);
        item.setId(itemID);
        item.setOwner(userId);
        if (item.getAvailable() != null) {
            itemRepo.updateAvailable(item.getId(),item.getAvailable());
        }
        if (item.getDescription() != null) {
            itemRepo.updateDescription(item.getId(), item.getDescription());
        }
        if (item.getName() != null) {
            itemRepo.updateName(item.getId(), item.getName());
        }
            return ItemMapper.toItemDto(itemRepo.findById(item.getId()).orElseThrow(() -> {
                throw new ItemNotFoundException("Item " + itemID + " not found");
            }));
        }

    @Override
    public ItemWithLastAndNextBookingAndComments getItem(int userId, int itemId) {
        boolean isOwner;
        try {
            checkIfUserIsOwner(userId, itemId);
            isOwner = true;
        } catch (NonOwnerUpdatingException e) {
            isOwner = false;
        }
        LocalDateTime now = LocalDateTime.now();
        return itemRepo.findItemWithLastAndNextBookingAndComments(itemId, now, isOwner);
    }

    @Override
    public List<ItemWithLastAndNextBookingAndComments> getItems(int userId, int from, int size) throws UserNotFoundException {
        LocalDateTime now = LocalDateTime.now();
        return itemRepo.findAllWithLastAndNextBookingAndComments(userId, now, new ShareitPageRequest(from, size, Sort.by(Sort.Direction.DESC,"item.id")));
    }

    @Override
    public List<ItemDto> search(String text, int from, int size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepo.findAllByDescriptionContainingIgnoreCaseOrNameContainingIgnoreCase(text,
                        text, new ShareitPageRequest(from, size)).stream()
                .filter(Item::getAvailable)
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public CommentWithAuthorName addComment(int userId, int itemId, CommentDto commentDto) {
        LocalDateTime now = LocalDateTime.now();
        checkIfCommentRelatedToCurrentBooking(userId, itemId, now);
        Comment newComment = CommentMapper.mapDtoToModel(commentDto);
        newComment.setItem(itemRepo.findById(itemId)
                .orElseThrow(() -> {
                    throw new ItemNotFoundException("Item " + itemId + " does not exist");
                }));
        newComment.setAuthor(userRepo.findById(userId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException("User " + userId + " does not exist");
                }));
        newComment.setCreated(now);
        Comment addedComment = commentRepo.save(newComment);
        return commentRepo.findWithAuthorName(addedComment.getId());
    }
}
