package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserDuplicateException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage userStorage) {
        this.userStorage = userStorage;
    }


    @Override
    public List<User> getAllUsers() {
        return (List<User>) userStorage.getAllUsers();
    }

    @Override
    public User getUserById(Integer userId) throws UserNotFoundException {
        return userStorage.getUserById(userId);
    }

    @Override
    public User addUser(User user) throws UserDuplicateException, UserNotFoundException {
        checkForDuplicate(user);
        user.setId(userStorage.getAllUsers().size()+1);
        return userStorage.addUser(user);
    }

    @Override
    public User updateUser(User user) throws UserNotFoundException, UserDuplicateException {

        if (user.getEmail()==null) {
            user.setEmail(userStorage.getUserById(user.getId()).getEmail());
        }
        if (user.getName()==null) {
            user.setName(userStorage.getUserById(user.getId()).getName());
        }
        checkForDuplicate(user);
        return userStorage.updateUser(user);
    }

    @Override
    public void deleteUserById(Integer userId) throws UserNotFoundException {
        userStorage.deleteUser(userId);
    }

    @Override
    public boolean checkUser(int userId) {
        try {
            userStorage.getUserById(userId);
            return true;
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    private void checkForDuplicate(User user) throws UserDuplicateException {
        List<User> userList = (List<User>) userStorage.getAllUsers();
        if (userList!=null) {
            for (User user1 : userList) {
                if (user1.getEmail().equals(user.getEmail()) & user1.getId() != user.getId()) {
                    throw new UserDuplicateException();
                }
            }
        }
    }
}
