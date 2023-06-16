package ru.practicum.server.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.model.Comment;

@UtilityClass
public class CommentMapper {
    public Comment mapDtoToModel(CommentDto commentsDto) {
        Comment comment = new Comment();
        comment.setText(commentsDto.getText());
        return comment;
    }
}
