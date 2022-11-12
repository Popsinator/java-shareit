package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserRepositoryLocal {

    User getUser(int userId);

    Collection<User> findAllUsers();

    void deleteUser(int userId);

    User createUser(User user);

    User updateUser(User user, int userId);

    void checkUser(User user);
}
