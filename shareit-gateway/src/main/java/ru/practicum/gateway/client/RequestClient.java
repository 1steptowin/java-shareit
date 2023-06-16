package ru.practicum.gateway.client;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.practicum.gateway.client.domain.DomainClient;
import ru.practicum.gateway.dto.item.ItemRequestDto;
import ru.practicum.gateway.dto.item.ItemRequestWithItemsDto;
import ru.practicum.gateway.exception.MonoException;

import java.util.List;

@Service
public class RequestClient extends DomainClient {
    private static final String REQUEST_URI = "/requests/";
    private static final String USER_HEADER = "X-Sharer-User-Id";

    public Mono<ItemRequestDto> addItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        return webClient.post()
                .uri(String.format("%s%s", baseUrl, REQUEST_URI))
                .bodyValue(itemRequestDto)
                .header(USER_HEADER, userId.toString())
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MonoException(errorBody, response.statusCode())));
                    } else {
                        return response.bodyToMono(ItemRequestDto.class);
                    }
                });
    }

    public Mono<List<ItemRequestWithItemsDto>> getUsersRequestsWithItems(Long userId) {
        return webClient.get()
                .uri(String.format("%s%s", baseUrl, REQUEST_URI))
                .header(USER_HEADER, userId.toString())
                .retrieve().bodyToFlux(ItemRequestWithItemsDto.class).collectList();
    }

    public Mono<ItemRequestWithItemsDto> getRequestByIdWithItems(Long userId, Long requestId) {
        return webClient.get()
                .uri(String.format("%s%s%d", baseUrl, REQUEST_URI, requestId))
                .header(USER_HEADER, userId.toString())
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MonoException(errorBody, response.statusCode())));
                    } else {
                        return response.bodyToMono(ItemRequestWithItemsDto.class);
                    }
                });
    }

    public Mono<List<ItemRequestWithItemsDto>> getAllRequestsOfOtherUsers(Long userId, int from, int size) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("%s%s%s", baseUrl, REQUEST_URI, "all"))
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header(USER_HEADER, userId.toString())
                .retrieve().bodyToFlux(ItemRequestWithItemsDto.class).collectList();
    }
}
