package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.booking.dto.LastBooking;
import ru.practicum.shareit.booking.dto.NextBooking;
import ru.practicum.shareit.item.dto.CommentInListItem;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {

    private int id;

    @NotNull(groups = Marker.OnCreate.class)
    @NotEmpty(groups = Marker.OnCreate.class)
    private String name;

    @NotNull(groups = Marker.OnCreate.class)
    private String description;

    @NotNull(groups = Marker.OnCreate.class)
    private Boolean available;

    private UserDto owner;

    private int requestId;

    private LastBooking lastBooking;

    private NextBooking nextBooking;

    private List<CommentInListItem> comments = new ArrayList<>();

    public ItemDto(String name, String description, Boolean available, UserDto owner, int requestId) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.requestId = requestId;
    }

    public ItemDto(int id, String name, String description, Boolean available, LastBooking lastBooking, NextBooking nextBooking) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.lastBooking = lastBooking;
        this.nextBooking = nextBooking;
    }
}
