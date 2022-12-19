package ru.practicum.shareit.requests;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRequestReporistory extends JpaRepository<ItemRequest, Long> {

    ItemRequest findItemRequestByIdEquals(int requesterId);

    List<ItemRequest> findByRequester_Id(int userId);

    Page<ItemRequest> findAllByRequester_IdNot(int userId, Pageable pageable);

    boolean existsById(int requestId);
}
