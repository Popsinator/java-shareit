package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {


    private Integer id;

    @NotNull(groups = Marker.OnCreate.class)
    private String text;

    private ItemDto item;

    private UserDto author;
}
