package ru.practicum.shareit.item.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.projection.CommentWithAuthorName;

import java.util.List;

@Repository
public interface CommentRepo extends JpaRepository<Comment, Long> {
    @Query("select new ru.practicum.shareit.item.projection.CommentWithAuthorName(c.id, c.text, c.author.name, " +
            "c.created) from Comment c where c.id = ?1")
    CommentWithAuthorName findWithAuthorName(Long commentId);

    @Query("select new ru.practicum.shareit.item.projection.CommentWithAuthorName(c.id, c.text, c.author.name, " +
            "c.created) from Comment c where c.item.id = ?1")
    List<CommentWithAuthorName> findAllWithAuthorNameByItemId(int itemId);
}
