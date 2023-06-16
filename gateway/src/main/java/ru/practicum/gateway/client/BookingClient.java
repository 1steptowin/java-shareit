package ru.practicum.gateway.client;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.practicum.gateway.client.domain.DomainClient;
import ru.practicum.gateway.dto.booking.BookingRequestDto;
import ru.practicum.gateway.dto.booking.BookingResponseDto;
import ru.practicum.gateway.dto.booking.BookingStatus;
import ru.practicum.gateway.exception.MonoException;
import ru.practicum.gateway.exception.WrongStatusException;

import java.util.List;

@Service
public class BookingClient extends DomainClient {
    private static final String BOOKING_URI = "/bookings/";
    private static final String USER_HEADER = "X-Sharer-User-Id";

    private void checkStatus(String state) {
        try {
            BookingStatus.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new WrongStatusException(String.format("Unknown state: %s", state));
        }
    }

    public Mono<BookingResponseDto> addBooking(Long userId, BookingRequestDto bookingRequestDto) {
        return webClient.post()
                .uri(String.format("%s%s", baseUrl, BOOKING_URI))
                .bodyValue(bookingRequestDto)
                .header(USER_HEADER, userId.toString())
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MonoException(errorBody, response.statusCode())));
                    } else {
                        return response.bodyToMono(BookingResponseDto.class);
                    }
                });
    }

    public Mono<BookingResponseDto> setBookingStatus(Long userId, Long id, Boolean approved) {
        return webClient.patch()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("%s%s%d", baseUrl, BOOKING_URI, id))
                        .queryParam("approved", approved)
                        .build())
                .header(USER_HEADER, userId.toString())
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MonoException(errorBody, response.statusCode())));
                    } else {
                        return response.bodyToMono(BookingResponseDto.class);
                    }
                });
    }

    public Mono<BookingResponseDto> getBookingById(Long userId, Long id) {
        return webClient.get()
                .uri(String.format("%s%s%d", baseUrl, BOOKING_URI, id))
                .header(USER_HEADER, userId.toString())
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MonoException(errorBody, response.statusCode())));
                    } else {
                        return response.bodyToMono(BookingResponseDto.class);
                    }
                });
    }

    public Mono<List<BookingResponseDto>> getAllBookingsOfBookerByState(Long bookerId, String state, int from, int size) {
        checkStatus(state);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("%s%s", baseUrl, BOOKING_URI))
                        .queryParam("state", state)
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header(USER_HEADER, bookerId.toString())
                .retrieve().bodyToFlux(BookingResponseDto.class).collectList();
    }

    public Mono<List<BookingResponseDto>> getAllBookingsOfOwnerByState(Long ownerId, String state, int from, int size) {
        checkStatus(state);
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(String.format("%s%s%s", baseUrl, BOOKING_URI, "owner"))
                        .queryParam("state", state)
                        .queryParam("from", from)
                        .queryParam("size", size)
                        .build())
                .header(USER_HEADER, ownerId.toString())
                .retrieve().bodyToFlux(BookingResponseDto.class).collectList();
    }
}
