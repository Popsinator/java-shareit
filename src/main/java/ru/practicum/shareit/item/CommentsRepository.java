package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAll();

    List<Comment> findAllByItemIdEquals(int itemId);

    Comment save(Comment comment);
}
