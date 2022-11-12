package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;

import java.util.Collection;

public interface ItemService {

    Item createItem(Item item, int userId);

    Item updateItem(Item item, int itemId, Integer userId);

    Item getItem(int itemId, int userId);

    Collection<Item> getAllItems(Integer userId);

    Collection<Item> findItemsOnDescription(String text);

    Collection<Item> findAllItem();

    void deleteItem(int itemId);

    CommentDto createComment(Comment comment, int userId, int itemId);
}
