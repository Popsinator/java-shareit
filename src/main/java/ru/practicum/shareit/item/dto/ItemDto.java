package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class ItemDto {

    private int id;
    private String name;
    private String description;
    private Boolean available;
    private Integer owner;
    private Integer request;

    public ItemDto(String name, String description, Boolean available, Integer owner, Integer request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }
}
