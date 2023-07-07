package ru.practicum.gateway.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.practicum.gateway.client.domain.DomainClient;
import ru.practicum.gateway.dto.user.User;
import ru.practicum.gateway.exception.MonoException;

import java.util.List;

@Service
public class UserClient extends DomainClient {
    private static final String USER_URI = "/users/";

    public Mono<User> addUser(User user) {
        return webClient.post()
                .uri(String.format("%s%s", baseUrl, USER_URI))
                .bodyValue(user)
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MonoException(errorBody, response.statusCode())));
                    } else {
                        return response.bodyToMono(User.class);
                    }
                });
    }

    public Mono<User> updateUser(Long userId, User user) {
        return webClient.patch()
                .uri(String.format("%s%s%d", baseUrl, USER_URI, userId))
                .bodyValue(user)
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MonoException(errorBody, response.statusCode())));
                    } else {
                        return response.bodyToMono(User.class);
                    }
                });
    }

    public Mono<User> getUserById(Long userId) {
        return webClient.get()
                .uri(String.format("%s%s%d", baseUrl, USER_URI, userId))
                .exchangeToMono(response -> {
                    if (response.statusCode().isError()) {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new MonoException(errorBody, response.statusCode())));
                    } else {
                        return response.bodyToMono(User.class);
                    }
                });
    }

    public Mono<List<User>> getAllUsers() {
        return webClient.get()
                .uri(String.format("%s%s", baseUrl, USER_URI))
                .retrieve().bodyToFlux(User.class).collectList();
    }

    public Mono<Void> deleteUserById(Long userId) {
        return webClient.delete()
                .uri(String.format("%s%s%d", baseUrl, USER_URI, userId))
                .retrieve()
                .onStatus(HttpStatus::isError, response -> response.bodyToMono(String.class)
                        .flatMap(errorBody -> Mono.error(new MonoException(errorBody, response.statusCode()))))
                .bodyToMono(Void.class);
    }
}
