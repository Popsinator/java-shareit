package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentDto;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName() != null ? item.getName() : null,
                item.getDescription() != null ? item.getDescription() : null,
                item.getAvailable() != null ? item.getAvailable() : null,
                item.getOwner() != null ? item.getOwner() : null,
                item.getRequestId() != 0 ? item.getRequestId() : 0
        );
    }

    public static Item toDtoItem(ItemDto itemDto, Item item) {
        return new Item(
                itemDto.getName() == null ? item.getName() : itemDto.getName(),
                itemDto.getDescription() == null ? item.getDescription() : itemDto.getDescription(),
                itemDto.getAvailable() == null ? item.getAvailable() : itemDto.getAvailable(),
                itemDto.getOwner() == null ? item.getOwner() : itemDto.getOwner(),
                itemDto.getRequestId() == 0 ? item.getRequestId() : itemDto.getRequestId()
        );
    }

    public static CommentDto toDtoComment(Comment comment, String authorName, String created) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                authorName,
                created
        );
    }
}
