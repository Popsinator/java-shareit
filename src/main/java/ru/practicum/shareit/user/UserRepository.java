package ru.practicum.shareit.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findUserByIdEquals(int id);

    void removeUserByIdEquals(int userId);

    boolean existsById(int id);
}
