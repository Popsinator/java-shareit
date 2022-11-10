package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.user.User;

@Getter
@Setter
public class ItemDto {

    private int id;
    private String name;
    private String description;
    private Boolean available;
    private User owner;
    private ItemRequest request;

    public ItemDto(String name, String description, Boolean available, User owner, ItemRequest request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }
}
