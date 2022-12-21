package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.exception.BadRequestException;

import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping()
    public Booking create(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody BookingDtoIn booking) {
        return bookingService.createBooking(userId, booking);
    }

    @PatchMapping(path = "/{bookingId}")
    public BookingDto confirmBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                     @PathVariable String bookingId, @RequestParam(required = false) String approved) {
        if (approved == null) {
            throw new BadRequestException("Отсутствует статус в заголовке");
        }
        return bookingService.changeStatusOnApprovedOrRejected(Integer.parseInt(bookingId), userId, approved);
    }

    @GetMapping(path = "/{bookingId}")
    public BookingDto get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                          @PathVariable String bookingId) {
        return bookingService.getBookingDto(Integer.parseInt(bookingId), userId);
    }

    @GetMapping()
    public Collection<BookingDto> getAllBookingForUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                       @RequestParam(required = false) String state,
                                                       @RequestParam(required = false) String from,
                                                       @RequestParam(required = false) String size) {

        if (from == null || size == null) {
            from = "";
            size = "";
        } else {
            checkPagination(from, size);
        }
        return bookingService.getAllBookings(userId, state, from, size);
    }

    @GetMapping(path = "/owner")
    public Collection<BookingDto> getAllBookingForUserOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                            @RequestParam(required = false) String state,
                                                            @RequestParam(required = false) String from,
                                                            @RequestParam(required = false) String size) {
        //checkPagination(from, size);
        if (from == null || size == null) {
            from = "";
            size = "";
        }  else {
            checkPagination(from, size);
        }
        return bookingService.getAllBookingsOwner(userId, state, from, size);
    }

    public void checkPagination(String from, String size) {
        if ((Integer.parseInt(from) == 0 && Integer.parseInt(size) == 0)
                || Integer.parseInt(from) < 0 || Integer.parseInt(size) < 0) {
            throw new BadRequestException("Некорректные параметры пагинации.");
        }
    }
}
