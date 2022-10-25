package ru.practicum.shareit.item;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmptyFieldItemException;
import ru.practicum.shareit.exception.NotFoundOwnerItemException;
import ru.practicum.shareit.user.UserRepositoryImpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Component
public class ItemRepositoryImpl implements ItemRepository {

    private static final Map<Integer, Item> itemStorage = new HashMap<>();

    private static int identificator = 0;

    private ItemMapper itemMapper = new ItemMapper();

    public static Map<Integer, Item> getItemStorage() {
        return itemStorage;
    }

    @Override
    public Item createItem(Item item) {
        checkItem(item);
        identificator++;
        item.setId(identificator);
        itemStorage.put(identificator, item);
        return item;
    }

    @Override
    public Item updateItem(Item item, int itemId) {
        Item itemUpdate = itemMapper.toDtoItem(itemMapper.toItemDto(item), itemId);
        itemUpdate.setId(itemId);
        checkItem(itemUpdate);
        itemStorage.put(itemId, itemUpdate);
        return itemUpdate;
    }

    @Override
    public void deleteItem(int id) {
        itemStorage.remove(id);
    }

    @Override
    public Item getItem(int itemId) {
        return itemStorage.get(itemId);
    }

    @Override
    public Collection<Item> searchAllItemUser(Integer userId) {
        return itemStorage.values().stream()
                .filter(x -> x.getOwner() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> searchAllItemsOnDescription(String text) {
        return itemStorage.values().stream()
                .filter(x -> x.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(y -> y.getAvailable() == true)
                .collect(Collectors.toList());
    }


    @Override
    public void checkItem(Item item) {
        if(Objects.equals(item.getName(), "") || item.getDescription() == null || item.getAvailable() == null || item.getOwner() == 0) {
            throw new EmptyFieldItemException("Отсутствует имя, описание, статус или владелец");
        } else
            if(!UserRepositoryImpl.getUsersStorage().containsKey(item.getOwner())) {
            throw new NotFoundOwnerItemException(String.format(
                    "Владельца с идентификатором %s не существует.", item.getOwner()));
        }
    }
}
