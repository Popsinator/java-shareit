package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
	private final BookingClient bookingClient;

	/*@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") int userId,
			@PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBooking(userId, bookingId);
	}*/

	@PostMapping()
	public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody BookingDtoIn booking) {
		return bookingClient.createBooking(userId, booking);
	}

	@PatchMapping(path = "/{bookingId}")
	public ResponseEntity<Object> confirmBooking(@RequestHeader("X-Sharer-User-Id") Integer userId,
									 @PathVariable String bookingId, @RequestParam(required = false) String approved) {
		return bookingClient.changeStatusOnApprovedOrRejected(Integer.parseInt(bookingId), userId, approved);
	}

	@GetMapping(path = "/{bookingId}")
	public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Integer userId,
						  @PathVariable String bookingId) {
		return bookingClient.getBookingDto(Integer.parseInt(bookingId), userId);
	}

	@GetMapping()
	public ResponseEntity<Object> getAllBookingForUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
													   @RequestParam(required = false) String state,
													   @RequestParam(required = false) String from,
													   @RequestParam(required = false) String size) {
		if ((from == null || size == null) && state == null) {
			return bookingClient.getAllBookingsWithoutParametersAndPagination("", userId);
		} else if (state != null) {
			return bookingClient.getAllBookingsWithoutPagination("", userId, state);
		} else {
			return bookingClient.getAllBookingsWithPagination("", userId, from, size);
		}
	}

	@GetMapping(path = "/owner")
	public ResponseEntity<Object> getAllBookingForUserOwner(@RequestHeader("X-Sharer-User-Id") Integer userId,
															@RequestParam(required = false) String state,
															@RequestParam(required = false) String from,
															@RequestParam(required = false) String size) {
		if ((from == null || size == null) && state == null) {
			return bookingClient.getAllBookingsWithoutParametersAndPagination("/owner", userId);
		} else if ((from == null || size == null) && state != null) {
			return bookingClient.getAllBookingsWithoutPagination("/owner", userId, state);
		} else {
			return bookingClient.getAllBookingsWithPagination("/owner", userId, from, size);
		}

		/*if (from == null || size == null) {
			from = "";
			size = "";
		}*/
		//return bookingClient.getAllBookingsOwner(userId, state, from, size);
	}
}
