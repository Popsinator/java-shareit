package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmptyHeaderUserId;
import ru.practicum.shareit.exception.InvalidHeaderUserId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepositoryImpl repository;

    @Override
    public Item createItem(Item item, int userId) {
        item.setOwner(userId);
        return repository.createItem(item);
    }

    @Override
    public Item updateItem(Item item, int itemId, Integer userId) {
        if (userId == null) {
            throw new EmptyHeaderUserId("Отсутствует заголовок 'X-Sharer-User-Id'");
        } else
            if (!Objects.equals(repository.getItemStorage().get(itemId).getOwner(), userId)) {
            throw new InvalidHeaderUserId("Некорректный владелец item в заголовке 'X-Sharer-User-Id'");
        }
        item.setId(itemId);
        return repository.updateItem(item, itemId);
    }

    @Override
    public Item getItem(int itemId) {

        return repository.getItem(itemId);

    }

    @Override
    public Collection<Item> getItems(Integer userId) {

        return repository.searchAllItemUser(userId);
    }

    @Override
    public Collection<Item> findItemsOnDescription(String text) {
        if (text.isEmpty()) {
            List<Item> empty = new ArrayList<>();
            return empty;
        }
        return repository.searchAllItemsOnDescription(text);
    }

    @Override
    public void deleteItem(int itemId) {
        repository.deleteItem(itemId);
    }
}
