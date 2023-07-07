package ru.practicum.server.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.server.item.model.Comment;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.projection.CommentWithAuthorName;
import ru.practicum.server.item.repo.CommentRepo;
import ru.practicum.server.item.repo.ItemRepo;
import ru.practicum.server.request.model.ItemRequest;
import ru.practicum.server.request.repo.RequestRepo;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repo.UserRepo;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentRepoTest {
    @Autowired
    TestEntityManager em;
    @Autowired
    UserRepo userRepo;
    @Autowired
    CommentRepo commentRepo;
    @Autowired
    ItemRepo itemRepo;
    @Autowired
    RequestRepo requestRepo;
    Item item;
    Comment comment;

    private User setUser() {
        User user = new User();
        user.setName("Test name");
        user.setEmail("user@mail.com");
        return user;
    }

    private ItemRequest setRequest(User user) {
        ItemRequest request = new ItemRequest();
        request.setDescription("Request for test item");
        request.setUser(user);
        return request;
    }

    private Item setItem(User user, ItemRequest request) {
        Item item = new Item();
        item.setName("Test item");
        item.setDescription("New available test item");
        item.setOwner(user.getId());
        item.setAvailable(true);
        item.setRequest(request);
        return item;
    }

    private Comment setComment(User user, Item item) {
        Comment commentPrep = new Comment();
        commentPrep.setText("Comment");
        commentPrep.setItem(item);
        commentPrep.setAuthor(user);
        commentPrep.setCreated(LocalDateTime.now());
        return commentPrep;
    }

    @BeforeEach
    void setUp() {
        User user = setUser();
        userRepo.save(user);
        ItemRequest request = setRequest(user);
        requestRepo.save(request);
        item = setItem(user, request);
        itemRepo.save(item);
        comment = setComment(user, item);
        commentRepo.save(comment);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(em);
    }

    @Test
    void testFindWithAuthorName() {
        CommentWithAuthorName commentWithAuthorName = commentRepo.findWithAuthorName(comment.getId());
        Assertions.assertEquals(comment.getId(), commentWithAuthorName.getId());
        Assertions.assertEquals("Comment", commentWithAuthorName.getText());
        Assertions.assertEquals("Test name", commentWithAuthorName.getAuthorName());
    }

    @Test
    void testFindAllWithAuthorNameByItemId() {
        List<CommentWithAuthorName> comments = commentRepo.findAllWithAuthorNameByItemId(item.getId());
        Assertions.assertEquals(1, comments.size());
        Assertions.assertEquals(1L, comments.get(0).getId());
        Assertions.assertEquals("Comment", comments.get(0).getText());
        Assertions.assertEquals("Test name", comments.get(0).getAuthorName());
    }
}
