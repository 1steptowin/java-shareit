package ru.practicum.gateway.dto.comments;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentWithAuthorName {
    Long id;
    String text;
    String authorName;
    LocalDateTime created;
}
