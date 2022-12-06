package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findBookingByIdEquals(int bookingId);

    List<Booking> findAllByBooker_Id(int userId);

    Page<Booking> findAllByItemOwner_Id(int ownerId, Pageable pageable);

    Page<Booking> findAllByBooker_Id(int userId, Pageable pageable);
}
