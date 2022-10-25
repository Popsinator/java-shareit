package ru.practicum.shareit.item;

public class ItemMapper {

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
                itemDto.getName() == null ? ItemRepositoryImpl.getItemStorage().get(itemId).getName()
                        : itemDto.getName(),
                itemDto.getDescription() == null ? ItemRepositoryImpl.getItemStorage().get(itemId).getDescription()
                        : itemDto.getDescription(),
                itemDto.getAvailable() == null ? ItemRepositoryImpl.getItemStorage().get(itemId).getAvailable()
                        : itemDto.getAvailable(),
                itemDto.getOwner() == null ? ItemRepositoryImpl.getItemStorage().get(itemId).getOwner()
                        : itemDto.getOwner(),
                itemDto.getRequest() == null ? ItemRepositoryImpl.getItemStorage().get(itemId).getRequest()
                        : itemDto.getRequest()
            );
    }
}
