package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.IdItemOrUserNotExistException;
import ru.practicum.shareit.exception.InvalidMaleUserException;


import java.util.Collection;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Transactional
    @Override
    public User createUser(User user) {
        checkUser(user);
        return repository.save(user);
    }

    @Transactional
    @Override
    public User updateUser(User user, int userId) {
        User userUpdate = repository.findUserByIdEquals(userId);
        if (user.getName() == null) {
            userUpdate.setEmail(user.getEmail());
        } else if (user.getEmail() == null) {
            userUpdate.setName(user.getName());
        } else {
            userUpdate.setName(user.getName());
            userUpdate.setEmail(user.getEmail());
        }
        return repository.save(userUpdate);
    }

    @Override
    public User getUser(int userId) {
        if (!checkUserId(userId)) {
            throw new IdItemOrUserNotExistException(String.format(
                    "Пользователь с данным id %s не зарегистрирован.", userId));
        }
        return repository.findUserByIdEquals(userId);
    }

    @Transactional
    @Override
    public void deleteUser(int userId) {
        User userDelete = repository.findUserByIdEquals(userId);
        repository.removeUserByIdEquals(userDelete.getId());
    }

    @Override
    public Collection<User> findAllUsers() {
        return repository.findAll();
    }

    public void checkUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !(user.getEmail().contains("@"))) {
            throw new InvalidMaleUserException("Введенный email отсутствует или некорректен");
        }
    }

    public boolean checkUserId(int userId) {
        boolean isExistUser = false;
        for (User value : repository.findAll()) {
            if (value.getId() == userId) {
                isExistUser = true;
                break;
            }
        }
        return isExistUser;
    }
}
