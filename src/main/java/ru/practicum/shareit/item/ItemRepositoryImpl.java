package ru.practicum.shareit.item;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmptyFieldItemException;
import ru.practicum.shareit.exception.NotFoundObjectException;
import ru.practicum.shareit.user.UserRepositoryLocalImpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Component
public class ItemRepositoryImpl implements ItemRepositoryLocal {

    private final Map<Integer, Item> itemStorage = new HashMap<>();

    private static int identificator = 0;

    private UserRepositoryLocalImpl userRepository;

    public ItemRepositoryImpl(UserRepositoryLocalImpl userRepository) {
        this.userRepository = userRepository;
    }

    public static int getIdentificator() {
        return identificator;
    }

    public static void setIdentificator(int identificator) {
        ItemRepositoryImpl.identificator = identificator + 1;
    }

    @Override
    public Item createItem(Item item) {
        checkItem(item);
        setIdentificator(getIdentificator());
        item.setId(identificator);
        itemStorage.put(identificator, item);
        return item;
    }

    @Override
    public Item updateItem(Item item, int itemId) {
        Item itemInStorage = getItem(itemId);
        Item itemUpdate = ItemMapper.toDtoItem(ItemMapper.toItemDto(item), itemInStorage);
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
                .filter(x -> Objects.equals(x.getOwner().getId(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> searchAllItemsOnDescription(String text) {
        return itemStorage.values().stream()
                .filter(x -> x.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public void checkItem(Item item) {
        if (Objects.equals(item.getName(), "") || item.getDescription() == null || item.getAvailable() == null || item.getOwner().getId() == 0) {
            throw new EmptyFieldItemException("Отсутствует имя, описание, статус или владелец");
        } else if (!userRepository.getUsersStorage().containsKey(item.getOwner().getId())) {
            throw new NotFoundObjectException(String.format(
                    "Владельца с идентификатором %s не существует.", item.getOwner()));
        }
    }
}
