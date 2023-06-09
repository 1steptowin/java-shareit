package ru.practicum.server.request.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.server.ShareitPageRequest;
import ru.practicum.server.exception.RequestNotFoundException;
import ru.practicum.server.request.dto.ItemRequestDto;
import ru.practicum.server.request.dto.ItemRequestWithItemsDto;
import ru.practicum.server.request.mapper.ItemRequestMapper;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.repo.RequestRepo;
import ru.practicum.server.exception.UserNotFoundException;
import ru.practicum.server.user.repo.UserRepo;

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
        if (!userRepo.existsById(userId)) {
            throw new UserNotFoundException("User " + userId + " does not exist");
        }
    }

    private void checkIfRequestExists(Long requestId) {
        if (!requestRepo.existsById(requestId)) {
            throw new RequestNotFoundException("Request " + requestId + " does not exist");
        }
    }

    @Override
    public ItemRequestDto addItemRequest(int userId, ItemRequestDto itemRequestDto) {
        checkIfUserExists(userId);
        LocalDateTime now = LocalDateTime.now();
        ItemRequest newRequest = ItemRequestMapper.mapDtoToModel(itemRequestDto);
        newRequest.setCreated(now);
        newRequest.setUser(userRepo.findById(userId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException("User " + userId + " does not exist");
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
        return requestRepo.findAllByUser_IdNot(Math.toIntExact(userId), new ShareitPageRequest(from,size)).getContent().stream()
                .map(ItemRequestMapper::mapModelToDtoWithItems)
                .collect(Collectors.toList());
    }
}