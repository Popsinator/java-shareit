package ru.practicum.shareit.UsersTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    UserServiceImpl userServiceMock;

    private final User user = new User(1, "user", "user@user");

    private final User userErrorEmail = new User(1, "user", "");

    private final User userErrorId = new User(99, "user", "user@user");

    private final User userWithoutEmail = new User(1, "user", null);

    private final User userWithoutName = new User(1, null, "user@user");

    private final List<User> listUsers = List.of(user);

    private UserServiceImpl userService;

    @BeforeEach
    void set() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void createNewUserTest() {
        Mockito.when(userRepository.save(any()))
                .thenReturn(user);
        Assertions.assertEquals(user.getId(), userService.createUser(user).getId());
    }

    @Test
    void createNewUserWithErrorTest() {
        Mockito.when(userServiceMock.createUser(userErrorEmail))
                .thenThrow(new BadRequestException("Введенный email отсутствует или некорректен"));

        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> userServiceMock.createUser(userErrorEmail));

        Assertions.assertEquals("Введенный email отсутствует или некорректен", exception.getMessage());
    }

    @Test
    void updateUserTest() {
        Mockito.when(userRepository.save(user))
                .thenReturn(user);
        Mockito.when(userRepository.findUserByIdEquals(user.getId()))
                .thenReturn(user);
        Assertions.assertEquals(user.getId(), userService.updateUser(user, user.getId()).getId());
        Assertions.assertEquals(user.getName(), userService.updateUser(user, user.getId()).getName());
        Assertions.assertEquals(user.getEmail(), userService.updateUser(user, user.getId()).getEmail());
    }

    @Test
    void updateUserWithoutNameTest() {
        Mockito.when(userRepository.save(user))
                .thenReturn(user);
        Mockito.when(userRepository.findUserByIdEquals(user.getId()))
                .thenReturn(user);
        Assertions.assertEquals(user.getId(), userService.updateUser(userWithoutName, userWithoutName.getId()).getId());
        Assertions.assertEquals(user.getName(), userService.updateUser(userWithoutName, userWithoutName.getId()).getName());
        Assertions.assertEquals(user.getEmail(), userService.updateUser(userWithoutName, userWithoutName.getId()).getEmail());
    }

    @Test
    void updateUserWithoutEmailTest() {
        Mockito.when(userRepository.save(user))
                .thenReturn(user);
        Mockito.when(userRepository.findUserByIdEquals(user.getId()))
                .thenReturn(user);
        Assertions.assertEquals(user.getId(), userService.updateUser(userWithoutEmail, userWithoutEmail.getId()).getId());
        Assertions.assertEquals(user.getName(), userService.updateUser(userWithoutEmail, userWithoutEmail.getId()).getName());
        Assertions.assertEquals(user.getEmail(), userService.updateUser(userWithoutEmail, userWithoutEmail.getId()).getEmail());
    }

    @Test
    void getUserTest() {
        Mockito.when(userRepository.findUserByIdEquals(user.getId()))
                .thenReturn(user);
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(true);
        Assertions.assertEquals(user, userService.getUser(user.getId()));
    }

    @Test
    void getUserWithErrorTest() {
        Mockito.when(userRepository.existsById(anyInt()))
                .thenReturn(false);

        final NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class,
                () -> userService.getUser(userErrorId.getId()));

        Assertions.assertEquals(String.format(
                "Пользователь с данным id %s не зарегистрирован.", userErrorId.getId()), exception.getMessage());
    }

    @Test
    void deleteUserTest() {
        Mockito.when(userRepository.findUserByIdEquals(user.getId()))
                .thenReturn(user);
        userService.deleteUser(user.getId());
        Mockito.verify(userRepository, Mockito.times(1))
                .removeUserByIdEquals(user.getId());
    }

    @Test
    void getAllUserTest() {
        Mockito.when(userRepository.findAll())
                .thenReturn(listUsers);
        Assertions.assertEquals(listUsers.size(), userService.findAllUsers().size());
        Assertions.assertEquals(listUsers.get(0), new ArrayList<>(userService.findAllUsers()).get(0));
    }

    @Test
    void checkUserTest() {
        final BadRequestException exception = Assertions.assertThrows(
                BadRequestException.class,
                () -> userService.checkUser(userErrorEmail));

        Assertions.assertEquals("Введенный email отсутствует или некорректен", exception.getMessage());
    }
}
