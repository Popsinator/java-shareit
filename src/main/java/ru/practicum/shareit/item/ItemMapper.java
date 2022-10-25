package ru.practicum.shareit.item;

import com.fasterxml.jackson.annotation.JacksonInject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class ItemMapper {

    Map<Integer, Item> itemRepository;

    public ItemMapper(Map<Integer, Item> itemRepository) {
        this.itemRepository = itemRepository;
    }

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName() != null ? item.getName() : null,
                item.getDescription() != null ? item.getDescription() : null,
                item.getAvailable() != null ? item.getAvailable() : null,
                item.getOwner() != null ? item.getOwner() : null,
                item.getRequest() != null ? item.getRequest() : null
        );
    }

    public Item toDtoItem(ItemDto itemDto, int itemId) {
        return new Item(
                itemDto.getName() == null ? itemRepository.get(itemId).getName()
                        : itemDto.getName(),
                itemDto.getDescription() == null ? itemRepository.get(itemId).getDescription()
                        : itemDto.getDescription(),
                itemDto.getAvailable() == null ? itemRepository.get(itemId).getAvailable()
                        : itemDto.getAvailable(),
                itemDto.getOwner() == null ? itemRepository.get(itemId).getOwner()
                        : itemDto.getOwner(),
                itemDto.getRequest() == null ? itemRepository.get(itemId).getRequest()
                        : itemDto.getRequest()
            );
    }
}
