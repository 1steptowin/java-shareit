package ru.practicum.gateway.exception;

import lombok.Data;

@Data
public class GatewayErrorResponse {
    String error;

    public GatewayErrorResponse(final String error) {
        this.error = error;
    }
}
