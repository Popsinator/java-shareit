package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;

import java.util.Collection;
import java.util.List;

public interface BookingService {

    Booking createBooking(int userId, BookingDtoIn booking);

    BookingDto changeStatusOnApprovedOrRejected(int bookingId, int userId, String approved);

    Booking getBooking(int bookingId, int userId);

    BookingDto getBookingDto(int bookingId, int userId);

    List<BookingDto> getAllBookings(int userId, String state, String from, String size);

    List<BookingDto> getAllBookingsOwner(int userId, String state, String from, String size);
}
