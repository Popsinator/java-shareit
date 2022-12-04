package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserService {
    User createUser(User user);

    User updateUser(User user, int userId);

    User getUser(int userId);

    void deleteUser(int userId);

    Collection<User> findAllUsers();
}
