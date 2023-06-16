package ru.practicum.shareit.user.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update User u set u.name = ?2 where u.id = ?1")
    void updateUserName(int id, String name);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("update User u set u.email = ?2 where u.id = ?1")
    void updateUserEmail(int id, String email);
}
