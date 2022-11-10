package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.LastBooking;
import ru.practicum.shareit.booking.dto.NextBooking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking, User user, Item item) {
        return new BookingDto(
                booking.getId() == 0 ? 0 : booking.getId(),
                booking.getStart() == null ? null : booking.getStart(),
                booking.getEnd() == null ? null : booking.getEnd(),
                booking.getStatus() == null ? null : booking.getStatus(),
                user,
                item
        );
    }

    public static LastBooking toLastBooking(Booking booking) {
        return new LastBooking("last", booking.getId(), booking.getBooker().getId());
    }

    public static NextBooking toNextBooking(Booking booking) {
        return new NextBooking("next", booking.getId(), booking.getBooker().getId());
    }

    public static Booking toBookingDtoIn(BookingDtoIn booking, User user, Item item) {
        return new Booking(
                booking.getId() == 0 ? 0 : booking.getId(),
                booking.getStart() == null ? null : booking.getStart(),
                booking.getEnd() == null ? null : booking.getEnd(),
                booking.getStatus() == null ? null : booking.getStatus(),
                user,
                item
        );
    }
}
