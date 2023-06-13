package ru.practicum.shareit.user.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmptyEmailException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.repo.UserRepo;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {
    UserRepo userRepo;

    @Autowired
    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    @Override
    public User getUserById(Integer userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> {
                    throw new UserNotFoundException("User " + userId + " does not exist");
                });
    }

    @Transactional
    @Override
    public User addUser(User user) throws UserNotFoundException {
        if (user.getEmail() != null) return userRepo.save(user);
        else throw new EmptyEmailException("Email is empty");
    }

    @Transactional
    @Override
    public User updateUser(User user) {
        if (user.getName() != null) {
            userRepo.updateUserName(user.getId(), user.getName());
        }
        if (user.getEmail() != null) {
            if (!checkForDuplicate(user)) {
                userRepo.updateUserEmail(user.getId(), user.getEmail());
            }
        }

        return userRepo.findById(user.getId())
                .orElseThrow(() -> {
            throw new UserNotFoundException("User " + user.getId() + " does not exists");
        });
    }

    @Override
    public void deleteUserById(Integer userId) throws UserNotFoundException {
        userRepo.deleteById(userId);
    }

    private boolean checkForDuplicate(User user) {
        return userRepo.findById(user.getId())
                .orElseThrow(() -> {
                    throw new UserNotFoundException("User " + user.getId() + " does not exist");
                }).getEmail().equals(user.getEmail());
    }
}
