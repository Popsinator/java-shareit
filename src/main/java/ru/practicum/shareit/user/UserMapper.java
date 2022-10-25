package ru.practicum.shareit.user;

public class UserMapper {

    public UserDto toUserDtoWithoutName(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setId(user.getId());
        return userDto;
    }

    public UserDto toUserDtoWithoutEmail(User user) {
        UserDto userDto = new UserDto();
        userDto.setName(user.getName());
        userDto.setId(user.getId());
        return userDto;
    }

    public User toDtoUserWithoutEmail(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public User toDtoUserWithoutName(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }
}
