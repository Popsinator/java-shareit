package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Transactional
    @Override
    public User createUser(User user) {
        return repository.save(user);
    }

    @Transactional
    @Override
    public User updateUser(User user, int userId) {
        Optional<User> userUpdate = repository.findUserByIdEquals(userId);
        if (user.getName() == null) {
            userUpdate.get().setEmail(user.getEmail());
        } else if (user.getEmail() == null) {
            userUpdate.get().setName(user.getName());
        } else {
            userUpdate.get().setName(user.getName());
            userUpdate.get().setEmail(user.getEmail());
        }
        return repository.save(userUpdate.get());
    }

    @Override
    public User getUser(int userId) {
        return repository.findUserByIdEquals(userId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Пользователь с данным id %s не зарегистрирован.", userId)));
    }

    @Transactional
    @Override
    public void deleteUser(int userId) {
        User userDelete = repository.findUserByIdEquals(userId).get();
        repository.removeUserByIdEquals(userDelete.getId());
    }

    @Override
    public List<User> findAllUsers() {
        return repository.findAll();
    }

    public void checkUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !(user.getEmail().contains("@"))) {
            throw new BadRequestException("Введенный email отсутствует или некорректен");
        }
    }
}
