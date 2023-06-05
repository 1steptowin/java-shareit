package ru.practicum.shareit.booking.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.projection.BookingShort;
import ru.practicum.shareit.booking.repo.BookingRepo;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.repo.ItemRepo;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.repo.UserRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookingServiceImpl implements BookingService {
    BookingRepo bookingRepo;
    UserRepo userRepo;
    ItemRepo itemRepo;
    Supplier<BookingNotFoundException> bookingNotFoundSupplier =
            () -> {
                throw new BookingNotFoundException("Booking does not exist");
            };
    Supplier<UserNotFoundException> userNotFoundSupplier =
            () -> {
                throw new UserNotFoundException("User does not exist");
            };
    Supplier<ItemNotFoundException> itemNotFoundSupplier =
            () -> {
                throw new ItemNotFoundException("Item does not exist");
            };
    Function<LocalDateTime, Predicate<BookingShort>> currentBookingsFunction = now ->
            b -> b.getStart().isBefore(now) && b.getEnd().isAfter(now);
    Function<LocalDateTime, Predicate<BookingShort>> pastBookingsFunction = now ->
            b -> b.getEnd().isBefore(now);
    Function<LocalDateTime, Predicate<BookingShort>> futureBookingsFunction = now ->
            b -> b.getStart().isAfter(now);


    @Autowired
    public BookingServiceImpl(BookingRepo bookingRepo, UserRepo userRepo, ItemRepo itemRepo) {
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
        this.itemRepo = itemRepo;
    }

    private void checkIfItemIsAvailable(int itemId) {
        if (!itemRepo.findById(itemId).orElseThrow(bookingNotFoundSupplier).getAvailable()) {
            throw new ItemUnavailableException("Item is not available");
        }
    }

    private void checkStartAndEnd(LocalDateTime start, LocalDateTime end) {
        if (start.isBefore(LocalDateTime.now()) || end.isBefore(LocalDateTime.now())) {
            throw new WrongDateException("Start or end in the past");
        }
        if (start.isEqual(end) || start.isAfter(end)) {
            throw new WrongDateException("Start has to precede end");
        }
    }

    private void checkIfOwnerIsApproving(int userId, Long id) {
        if (!(bookingRepo.findById(id).orElseThrow(bookingNotFoundSupplier).getItem().getOwner() == userId)) {
            throw new NonOwnerUpdatingException("Booking may be approved only by the owner");
        }
    }

    private void checkIfStatusUpdateIsBeforeApproval(long id) {
        if (!bookingRepo.findById(id).orElseThrow(bookingNotFoundSupplier).getStatus().equals(BookingStatus.WAITING)) {
            throw new UpdateStatusAfterApprovalException("Booking status may not be changed after approval");
        }
    }

    private void checkIfBookerOrOwnerIsRequesting(int userId, Long id) {
        int ownerId = bookingRepo.findById(id)
                .orElseThrow(bookingNotFoundSupplier)
                .getItem().getOwner();
        int bookerId = bookingRepo.findById(id)
                .orElseThrow(bookingNotFoundSupplier)
                .getBooker().getId();
        if (!(userId == (ownerId)) && !(userId == (bookerId))) {
            throw new NonOwnerUpdatingException("Booking may be viewed by its booker or owner");
        }
    }

    private void checkIfOwnerExists(int ownerId) {
        if (userRepo.findById(ownerId).isEmpty()) {
            throw new UserNotFoundException("User does not exist");
        }
    }

    private void checkIfBookingExists(Long bookingId) {
        if (bookingRepo.findById(bookingId).isEmpty()) {
            throw new BookingNotFoundException("Booking does not exist");
        }
    }

    private void checkIfBookerIsNotOwner(int userId, int itemId) {
        if (userId == (itemRepo.findById(itemId).orElseThrow(itemNotFoundSupplier).getOwner())) {
            throw new BookerIsOwnerException("Owner may not book his own item");
        }
    }

    private BookingStatus parseStatus(String state) {
        try {
            return BookingStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new WrongStatusException(String.format("Unknown state: %s", state));
        }
    }

    @Transactional
    @Override
    public BookingResponseDto addBooking(int userId, BookingRequestDto bookingRequestDto) {
        checkIfItemIsAvailable(bookingRequestDto.getItemId());
        checkStartAndEnd(bookingRequestDto.getStart(), bookingRequestDto.getEnd());
        checkIfBookerIsNotOwner(userId, bookingRequestDto.getItemId());
        Booking newBooking = BookingMapper.mapDtoToModel(bookingRequestDto);
        newBooking.setItem(itemRepo.findById(bookingRequestDto.getItemId())
                .orElseThrow(itemNotFoundSupplier));
        newBooking.setBooker(userRepo.findById(userId)
                .orElseThrow(userNotFoundSupplier));
        newBooking.setStatus(BookingStatus.WAITING);
        Booking addedBooking = bookingRepo.save(newBooking);
        return BookingMapper.mapModelToDto(addedBooking);
    }

    @Transactional
    @Override
    public BookingResponseDto setBookingStatus(int userId, Long id, Boolean approved) {
        checkIfOwnerIsApproving(userId, id);
        checkIfStatusUpdateIsBeforeApproval(id);
        if (approved) {
            bookingRepo.updateStatus(id, BookingStatus.APPROVED);
        } else {
            bookingRepo.updateStatus(id, BookingStatus.REJECTED);
        }
        Booking bookingUpdated = bookingRepo.findById(id).orElseThrow();
        return BookingMapper.mapModelToDto(bookingUpdated);
    }

    @Override
    public BookingResponseDto getBookingById(int userId, Long id) {
        checkIfBookerOrOwnerIsRequesting(userId, id);
        checkIfBookingExists(id);
        return BookingMapper.mapProjectionToDto(bookingRepo.findBookingShortByBookingId(id));
    }

    @Override
    public List<BookingResponseDto> getAllBookingsOfBookerByState(int bookerId, String state, int from, int size) {
        checkIfOwnerExists(bookerId);
        BookingStatus requestedStatus = parseStatus(state);
        LocalDateTime now = LocalDateTime.now();
        Pageable request = PageRequest.of(from > 0 ? from / size : 0, size);
        switch (requestedStatus) {
            case ALL:
                return bookingRepo.findAllByBookerIdOrderByStartDesc(bookerId, request).getContent().stream()
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
            case CURRENT:
                return bookingRepo.findAllByBookerIdOrderByStartDesc(bookerId, request).getContent().stream()
                        .filter(currentBookingsFunction.apply(now))
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
            case PAST:
                return bookingRepo.findAllByBookerIdOrderByStartDesc(bookerId, request).getContent().stream()
                        .filter(pastBookingsFunction.apply(now))
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepo.findAllByBookerIdOrderByStartDesc(bookerId, request).getContent().stream()
                        .filter(futureBookingsFunction.apply(now))
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
            default:
                return bookingRepo.findAllByBookerIdAndStatusOrderByStartDesc(bookerId, requestedStatus, request)
                        .getContent().stream()
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
        }
    }

    @Override
    public List<BookingResponseDto> getAllBookingsOfOwnerByState(int ownerId, String state, int from, int size) {
        checkIfOwnerExists(ownerId);
        BookingStatus requestedStatus = parseStatus(state);
        LocalDateTime now = LocalDateTime.now();
        Pageable request = PageRequest.of(from > 0 ? from / size : 0, size);
        switch (requestedStatus) {
            case ALL:
                return bookingRepo.findAllByOwnerIdOrderByStartDesc(ownerId, request).getContent().stream()
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
            case CURRENT:
                return bookingRepo.findAllByOwnerIdOrderByStartDesc(ownerId, request).getContent().stream()
                        .filter(currentBookingsFunction.apply(now))
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
            case PAST:
                return bookingRepo.findAllByOwnerIdOrderByStartDesc(ownerId, request).getContent().stream()
                        .filter(pastBookingsFunction.apply(now))
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
            case FUTURE:
                return bookingRepo.findAllByOwnerIdOrderByStartDesc(ownerId, request).getContent().stream()
                        .filter(futureBookingsFunction.apply(now))
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
            default:
                return bookingRepo.findAllByOwnerIdAndStatusOrderByStartDesc(ownerId, requestedStatus, request)
                        .getContent().stream()
                        .map(BookingMapper::mapProjectionToDto).collect(Collectors.toList());
        }
    }
}
