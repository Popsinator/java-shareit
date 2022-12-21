package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @PostMapping()
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> createNewUser(@Valid @RequestBody UserDto user) {
        return userClient.createUser(user);
    }

    @PatchMapping(path = "/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto user, @PathVariable String userId) {
        user.setId(Integer.parseInt(userId));
        return userClient.updateUser(user, Integer.parseInt(userId));
    }

    @GetMapping(path = "/{userId}")
    public ResponseEntity<Object> findUser(@PathVariable String userId) {
        return userClient.getUser(Integer.parseInt(userId));
    }

    @GetMapping()
    public ResponseEntity<Object> findAllUsers() {
        return userClient.findAllUsers();
    }

    @DeleteMapping(path = "/{userId}")
    public void deleteUser(@PathVariable String userId) {
        userClient.deleteUser(Integer.parseInt(userId));
    }
}
