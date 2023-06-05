package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmptyEmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.repo.UserRepo;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;

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
                    throw new UserNotFoundException("User does not exist");
                });
    }

    @Transactional
    @Override
    public User addUser(User user) throws UserNotFoundException {
        if (user.getEmail()!=null) {return userRepo.save(user);}
        else {throw new EmptyEmailException("Email is empty");}
    }

    @Transactional
    @Override
    public User updateUser(User user) {
        if (user.getName() != null) {
            userRepo.updateUserName(user.getId(), user.getName());
        }
        if (user.getEmail() != null) {
            if (!checkForDuplicate(user)){userRepo.updateUserEmail(user.getId(), user.getEmail());}
        }

        return userRepo.findById(user.getId())
                .orElseThrow(() -> {
            throw new UserNotFoundException("User does not exists");
        });
    }

    @Override
    public void deleteUserById(Integer userId) throws UserNotFoundException {
        userRepo.deleteById(userId);
    }

    @Override
    public void checkUser(int userId) {
        if (userRepo.findById(userId).isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
    }

    private boolean checkForDuplicate(User user) {
        return userRepo.findById(user.getId())
                .orElseThrow(() -> {
                    throw new UserNotFoundException("User does not exist");
                }).getEmail().equals(user.getEmail());
    }
}