package ru.practicum.shareit.user;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.InvalidMaleUserException;
import ru.practicum.shareit.exception.UserAlreadyExistException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Data
@Component
public class UserRepositoryImpl implements UserRepository {

    private static final Map<Integer, User> usersStorage = new HashMap<>();
    private static int id = 0;

    private UserMapper userMapper = new UserMapper();

    public static Map<Integer, User> getUsersStorage() {
        return usersStorage;
    }

    @Override
    public User getUser(int userId) {
        return usersStorage.get(userId);
    }

    @Override
    public Collection<User> findAllUsers() {
        return usersStorage.values();
    }

    @Override
    public void deleteUser(int userId) {
        usersStorage.remove(userId);
    }

    @Override
    public User createUser(User user) {
        checkUser(user);
        id++;
        user.setId(id);
        usersStorage.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user, int userId) {
        user.setId(userId);
        User userUpdate;
        if (user.getName() == null) {
            UserDto userDto = userMapper.toUserDtoWithoutName(user);
            userDto.setName(usersStorage.get(user.getId()).getName());
            userUpdate = userMapper.toDtoUserWithoutName(userDto);
            checkUser(userUpdate);
        } else
            if(user.getEmail() == null) {
            UserDto userDto = userMapper.toUserDtoWithoutEmail(user);
            userDto.setEmail(usersStorage.get(user.getId()).getEmail());
            userUpdate = userMapper.toDtoUserWithoutEmail(userDto);
        } else {
            userUpdate = user;
            checkUser(userUpdate);
        }
        usersStorage.put(userId, userUpdate);
        return userUpdate;
    }

    @Override
    public void checkUser(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !(user.getEmail().contains("@"))) {
            throw new InvalidMaleUserException("Введенный email отсутствует или некорректен");
        }
        for (User value : usersStorage.values()) {
            if (value.getEmail().equals(user.getEmail())) {
                throw new UserAlreadyExistException(String.format(
                        "Пользователь с данным email %s уже зарегистрирован.", user.getEmail()));
            }
        }
    }
}
