package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findBookingByIdEquals(int bookingId);

    List<Booking> findAllByBooker_Id(int userId);

    List<Booking> findAllByItem_Id(int itemId);

    List<Booking> findAllByItem_IdAndAndBooker_Id(int itemId, int bookerId);

    Page<Booking> findAllByItemOwner_Id(int ownerId, Pageable pageable);

    Page<Booking> findAllByBooker_Id(int userId, Pageable pageable);

    boolean existsById(int bookingId);
}
