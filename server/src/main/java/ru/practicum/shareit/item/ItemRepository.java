package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByDescriptionContainingIgnoreCase(String text);

    List<Item> findAllByRequestIdEquals(int requestId);

    Item findItemByIdEquals(int id);

    List<Item> findAllByOwner_Id(int userId);

    boolean existsById(int itemId);

    void deleteItemById(int id);
}
