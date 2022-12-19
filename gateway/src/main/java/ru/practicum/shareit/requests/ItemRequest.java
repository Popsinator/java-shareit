package ru.practicum.shareit.requests;

import lombok.*;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {

    private int id;

    private String description;

    private User requester;

    private LocalDateTime created;

    private List<Item> items;
}
