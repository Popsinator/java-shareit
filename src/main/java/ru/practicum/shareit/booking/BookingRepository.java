package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking save(Booking booking);

    Booking findBookingByIdEquals(int bookingId);

    Collection<Booking> findAllByBookerIdEquals(int bookerId);

    List<Booking> findAll();

    List<Booking> findAllByItemIdEquals(int itemId);

    List<Booking> findAllByItemIdEqualsAndBookerIdEquals(int itemId, int userId);
}
