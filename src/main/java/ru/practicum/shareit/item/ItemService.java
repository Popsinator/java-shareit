package ru.practicum.shareit.item;

import java.util.Collection;

public interface ItemService {

    Item createItem(Item item, int userId);

    Item updateItem(Item item, int itemId, Integer userId);

    Item getItem(int itemId);

    Collection<Item> getItems(Integer userId);

    Collection<Item> findItemsOnDescription(String text);

    void deleteItem(int itemId);
}
