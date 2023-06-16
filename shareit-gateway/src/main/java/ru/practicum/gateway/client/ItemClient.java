package ru.practicum.gateway.client;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.practicum.gateway.client.domain.DomainClient;
import ru.practicum.gateway.dto.comments.CommentWithAuthorName;
import ru.practicum.gateway.dto.comments.CommentsDto;
import ru.practicum.gateway.dto.item.ItemDto;
import ru.practicum.gateway.dto.item.ItemWithLastAndNextBookingAndComments;
import ru.practicum.gateway.exception.MonoException;

import java.util.List;

@Service
public class ItemClient extends DomainClient {
    private static final String ITEM_URI = "/items/";
    private static final String USER_HEADER = "X-Sharer-User-Id";

    public Mono<ItemDto> addItem(Long userId, ItemDto itemDto) {
        return webClient.post()
                .uri(String.format("%s%s", baseUrl, ITEM_URI))
                .bodyValue(itemDto)
                .header(USER_HEADER, userId.toString())
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MonoException(errorBody, response.statusCode())));
                    } else {
                        return response.bodyToMono(ItemDto.class);
                    }
                });
    }

    public Mono<CommentWithAuthorName> addComment(Long userId, Long itemId, CommentsDto commentsDto) {
        return webClient.post()
                .uri(String.format("%s%s%d%s", baseUrl, ITEM_URI, itemId, "/comment"))
                .bodyValue(commentsDto)
                .header(USER_HEADER, userId.toString())
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MonoException(errorBody, response.statusCode())));
                    } else {
                        return response.bodyToMono(CommentWithAuthorName.class);
                    }
                });
    }

    public Mono<ItemDto> updateItem(Long ownerId, Long itemId, ItemDto itemDto) {
        return webClient.patch()
                .uri(String.format("%s%s%d", baseUrl, ITEM_URI, itemId))
                .bodyValue(itemDto)
                .header(USER_HEADER, ownerId.toString())
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MonoException(errorBody, response.statusCode())));
                    } else {
                        return response.bodyToMono(ItemDto.class);
                    }
                });
    }

    public Mono<ItemWithLastAndNextBookingAndComments> getItemById(Long userId, Long itemId) {
        return webClient.get()
                .uri(String.format("%s%s%d", baseUrl, ITEM_URI, itemId))
                .header(USER_HEADER, userId.toString())
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MonoException(errorBody, response.statusCode())));
                    } else {
                        return response.bodyToMono(ItemWithLastAndNextBookingAndComments.class);
                    }
                });
    }

    public Mono<List<ItemWithLastAndNextBookingAndComments>> getAllOwnersItems(Long ownerId) {
        return webClient.get()
                .uri(String.format("%s%s", baseUrl, ITEM_URI))
                .header(USER_HEADER, ownerId.toString())
                .retrieve().bodyToFlux(ItemWithLastAndNextBookingAndComments.class).collectList();
    }

    public Mono<List<ItemDto>> searchItems(String text) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("%s%s%s", baseUrl, ITEM_URI, "search"))
                        .queryParam("text", text)
                        .build())
                .retrieve().bodyToFlux(ItemDto.class).collectList();
    }
}
