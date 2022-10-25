package ru.practicum.shareit.item;

import lombok.Data;
import ru.practicum.shareit.requests.ItemRequest;
@Data
public class Item {

    private int id;
    private String name;
    private String description;
    private Boolean available;
    private Integer owner;
    private ItemRequest request;

    public Item(String name, String description, Boolean available, Integer owner, ItemRequest request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.owner = owner;
        this.request = request;
    }
}
