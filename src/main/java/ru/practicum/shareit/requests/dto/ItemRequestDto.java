package ru.practicum.shareit.requests.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.List;

@Getter

@Setter

@AllArgsConstructor

@NoArgsConstructor
public class ItemRequestDto {

    private String description;

    private LocalDateTime created;

    private List<Item> items;
}
