package ru.practicum.shareit.requests;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestReporistory extends JpaRepository<ItemRequest, Long> {

    ItemRequest save(ItemRequest request);

    List<ItemRequest> findAll();

    ItemRequest findItemRequestByIdEquals(int requesterId);

    Page<ItemRequest> findAll(Pageable pageable);

    List<ItemRequest> findByRequester_Id(int userId);
}
