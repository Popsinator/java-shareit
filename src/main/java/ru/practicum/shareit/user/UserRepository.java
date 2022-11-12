package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAll();

    User findUserByIdEquals(int id);

    User save(User user);

    void removeUserByIdEquals(int userId);
}
