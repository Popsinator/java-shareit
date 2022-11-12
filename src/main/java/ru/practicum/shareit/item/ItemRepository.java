package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByDescriptionContainingIgnoreCase(String text);

    Item findItemByIdEquals(int id);

    List<Item> findAll();

    Item save(Item item);

    void deleteItemById(int id);
}
