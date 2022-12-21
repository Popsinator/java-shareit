package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

public interface ItemService {

    Item createItem(Item item, int userId);

    Item updateItem(Item item, int itemId, Integer userId);

    Item getItem(int itemId, int userId);

    List<Item> getAllItems(Integer userId);

    List<Item> findItemsOnDescription(String text);

    void deleteItem(int itemId);

    CommentDto createComment(Comment comment, int userId, int itemId);
}
