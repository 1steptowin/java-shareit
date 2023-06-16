package ru.practicum.server.user.dto;

import lombok.AccessLevel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;


@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class UserDto {
    String name;
    String email;
}