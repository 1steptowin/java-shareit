package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

@UtilityClass
public class CommentMapper {
    public Comment mapDtoToModel(CommentDto commentsDto) {
        Comment comment = new Comment();
        comment.setText(commentsDto.getText());
        return comment;
    }
}
