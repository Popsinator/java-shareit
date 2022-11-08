package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public User createUser(User user) {
        return repository.createUser(user);
    }

    @Override
    public User updateUser(User user, int userId) {
        return repository.updateUser(user, userId);
    }

    @Override
    public User getUser(int userId) {
        return repository.getUser(userId);
    }

    @Override
    public void deleteUser(int userId) {
        repository.deleteUser(userId);
    }

    @Override
    public Collection<User> findAllUsers() {
        return repository.findAllUsers();
    }
}
