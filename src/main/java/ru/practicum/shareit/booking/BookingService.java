package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.Collection;

public interface BookingService {

    BookingDto createBooking(int userId, Booking booking);

    BookingDto changeStatusOnApprovedOrRejected(int bookingId, int userId, String approved);

    Booking getBooking(int bookingId, int userId);

    BookingDto getBookingDto(int bookingId, int userId);

    Collection<BookingDto> getAllBookings(int userId, String state);

    Collection<BookingDto> getAllBookingsOwner(int userId, String state);
}
