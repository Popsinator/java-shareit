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

    private static final Map<Integer, User> usersStorage = new HashMap<>();//Хранилище пользователей
    private static int id = 0;//Id для идентификации пользователей

    private UserMapper userMapper = new UserMapper();//Объект для маппинга

    public static Map<Integer, User> getUsersStorage() {
        return usersStorage;
    }

    @Override
    public User getUser(int userId) {//Получение пользователя
        return usersStorage.get(userId);
    }

    @Override
    public Collection<User> findAllUsers() {//Получение пользователя
        return usersStorage.values();
    }

    @Override
    public void deleteUser(int userId) {//Удаление пользователя
        usersStorage.remove(userId);
    }

    @Override
    public User createUser(User user) {//Создание пользователей
        checkUser(user);//Проверка email
        id++;
        user.setId(id);
        usersStorage.put(id, user);
        return user;
    }

    @Override
    public User updateUser(User user, int userId) {
        user.setId(userId);
        User userUpdate;
        if (user.getName() == null) {//Для обновления email
            UserDto userDto = userMapper.toUserDtoWithoutName(user);
            userDto.setName(usersStorage.get(user.getId()).getName());
            userUpdate = userMapper.toDtoUserWithoutName(userDto);
            checkUser(userUpdate);
        } else if(user.getEmail() == null) {//Для обновления name
            UserDto userDto = userMapper.toUserDtoWithoutEmail(user);
            userDto.setEmail(usersStorage.get(user.getId()).getEmail());
            userUpdate = userMapper.toDtoUserWithoutEmail(userDto);
        } else {//Для обновления email и name
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
