package ru.practicum.shareit.item;

import java.util.Collection;

public interface ItemRepositoryLocal {

    Item createItem(Item item);

    Item updateItem(Item item, int itemId);

    void deleteItem(int id);

    Item getItem(int itemId);

    Collection<Item> searchAllItemUser(Integer userId);

    Collection<Item> searchAllItemsOnDescription(String text);

    void checkItem(Item item);
}
