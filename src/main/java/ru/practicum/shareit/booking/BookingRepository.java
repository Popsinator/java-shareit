package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking save(Booking booking);

    Booking findBookingByIdEquals(int bookingId);

    List<Booking> findAll();
}
