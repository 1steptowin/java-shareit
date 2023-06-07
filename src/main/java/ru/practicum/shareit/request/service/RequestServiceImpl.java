package ru.practicum.shareit.request.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithItemsDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repo.RequestRepo;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.repo.UserRepo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RequestServiceImpl implements RequestService {
    RequestRepo requestRepo;
    UserRepo userRepo;

    @Autowired
    public RequestServiceImpl(RequestRepo requestRepo, UserRepo userRepo) {
        this.requestRepo = requestRepo;
        this.userRepo = userRepo;
    }

    private void checkIfUserExists(int userId) {
        userRepo.findById(userId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException("User "+userId+" does not exist");
                });
    }

    private void checkIfRequestExists(Long requestId) {
        requestRepo.findById(requestId)
                .orElseThrow(() -> {
                    throw new RequestNotFoundException("Request "+requestId+" does not exist");
                });
    }

    @Override
    public ItemRequestDto addItemRequest(int userId, ItemRequestDto itemRequestDto) {
        checkIfUserExists(userId);
        LocalDateTime now = LocalDateTime.now();
        ItemRequest newRequest = ItemRequestMapper.mapDtoToModel(itemRequestDto);
        newRequest.setCreated(now);
        newRequest.setUser(userRepo.findById(userId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException("User "+userId+" does not exist");
                }));
        ItemRequest addedRequest = requestRepo.save(newRequest);
        return ItemRequestMapper.mapModelToDto(addedRequest);
    }

    @Override
    public List<ItemRequestWithItemsDto> getUsersRequestsWithItems(int userId) {
        checkIfUserExists(userId);
        return requestRepo.findAllByUser_Id(userId).stream()
                .map(ItemRequestMapper::mapModelToDtoWithItems)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestWithItemsDto getRequestByIdWithItems(int userId, Long requestId) {
        checkIfUserExists(userId);
        checkIfRequestExists(requestId);
        return ItemRequestMapper.mapModelToDtoWithItems(requestRepo.findAllById(requestId));
    }

    @Override
    public List<ItemRequestWithItemsDto> getAllRequestsOfOtherUsers(int userId, int from, int size) {
        checkIfUserExists(Math.toIntExact(userId));
        Pageable request = PageRequest.of(from > 0 ? from / size : 0, size);
        return requestRepo.findAllByUser_IdNot(Math.toIntExact(userId), request).getContent().stream()
                .map(ItemRequestMapper::mapModelToDtoWithItems)
                .collect(Collectors.toList());
    }
}