package ru.practicum.shareit.bookingsTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final Item item = new Item(1, "test", "Description test", true, null, null);

    private final User user = new User(1, "user", "user@user");

    private final Booking booking = new Booking(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.APPROVED, user, item);

    private final BookingDto bookingDto = new BookingDto(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), Status.APPROVED, user, item);

    private final List<BookingDto> listBookings = List.of(bookingDto);

    @Test
    void saveNewBookingTest() throws Exception {
        when(bookingService.createBooking(anyInt(), any()))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(booking))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId())))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    void updateBookingTest() throws Exception {
        when(bookingService.changeStatusOnApprovedOrRejected(anyInt(), anyInt(), anyString()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/" + bookingDto.getId())
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getBookingTest() throws Exception {
        when(bookingService.getBookingDto(anyInt(), anyInt()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/" + anyInt())
                        .header("X-Sharer-User-Id", anyInt())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getAllBookingForUserTest() throws Exception {
        when(bookingService.getAllBookings(anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(listBookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "1")
                        .param("from", "1")
                        .param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(listBookings.size())))
                .andExpect(jsonPath("[0].id", is(bookingDto.getId())))
                .andExpect(jsonPath("[0].status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void getAllBookingForOwnerTest() throws Exception {
        when(bookingService.getAllBookingsOwner(anyInt(), anyString(), anyString(), anyString()))
                .thenReturn(listBookings);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "1")
                        .param("from", "1")
                        .param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(listBookings.size())))
                .andExpect(jsonPath("[0].id", is(bookingDto.getId())))
                .andExpect(jsonPath("[0].status", is(bookingDto.getStatus().toString())));
    }
}
