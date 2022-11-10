package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping()
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody BookingDtoIn booking) {
        return bookingService.createBooking(userId, booking);
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingDto confirmBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @PathVariable String bookingId, @RequestParam(required = false) String approved) {
        return bookingService.changeStatusOnApprovedOrRejected(Integer.parseInt(bookingId), userId, approved);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingDto get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @PathVariable String bookingId) {
        return bookingService.getBookingDto(Integer.parseInt(bookingId), userId);
    }

    @GetMapping()
    public Collection<BookingDto> getAllBookingForUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                       @RequestParam(required = false) String state) {
        return bookingService.getAllBookings(userId, state);
    }

    @GetMapping(path = "/owner")
    public Collection<BookingDto> getAllBookingForUserOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                            @RequestParam(required = false) String state) {
        return bookingService.getAllBookingsOwner(userId, state);
    }
}
