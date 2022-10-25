package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping()//Добавление нового пользователя
    public User createNewUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PatchMapping(path = "/{userId}")//Обновление существующего пользователя
    public User updateUser(@RequestBody User user, @PathVariable String userId) {
        return userService.updateUser(user, Integer.parseInt(userId));
    }

    @GetMapping(path = "/{userId}")//Получение пользователя
    public User findUser(@PathVariable String userId) {
        return userService.getUser(Integer.parseInt(userId));
    }

    @GetMapping()//Получение пользователя
    public Collection<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @DeleteMapping(path = "/{userId}")//Удаление пользователя
    public void deleteUser(@PathVariable String userId) {
        userService.deleteUser(Integer.parseInt(userId));
    }
}
