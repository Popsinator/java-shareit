package ru.practicum.shareit.bookingsTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.exception.*;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerErrorTest {

    @Mock
    private BookingServiceImpl bookingService;

    @InjectMocks
    private BookingController controller;

    private final ObjectMapper mapper = new ObjectMapper();

    private MockMvc mvc;

    private final BookingDtoIn bookingNotExistItem = new BookingDtoIn(1, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), 99, Status.APPROVED);

    @BeforeEach
    void setUp() {
        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .setControllerAdvice(ErrorHandler.class)
                .build();
        mapper.registerModule(new JavaTimeModule());
    }

    @Test
    void saveNewBookingWithErrorItemTest() throws Exception {
        when(bookingService.createBooking(anyInt(), any()))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingNotExistItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveNewBookingWithItemBlockTest() throws Exception {
        when(bookingService.createBooking(anyInt(), any()))
                .thenThrow(BadRequestException.class);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingNotExistItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveNewBookingWithIncorrectDateTest() throws Exception {
        when(bookingService.createBooking(anyInt(), any()))
                .thenThrow(BadRequestException.class);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingNotExistItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void saveNewBookingWithIncorrectHeaderUserIdTest() throws Exception {
        when(bookingService.createBooking(anyInt(), any()))
                .thenThrow(NotFoundException.class);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(bookingNotExistItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBookingWithInvalidateStateTest() throws Exception {
        when(bookingService.changeStatusOnApprovedOrRejected(anyInt(), anyInt(), anyString()))
                .thenThrow(BadRequestException.class);

        mvc.perform(patch("/bookings/" + bookingNotExistItem.getId())
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingNotExistItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBookingWithAlreadyApprovedTest() throws Exception {
        when(bookingService.changeStatusOnApprovedOrRejected(anyInt(), anyInt(), anyString()))
                .thenThrow(BadRequestException.class);

        mvc.perform(patch("/bookings/" + bookingNotExistItem.getId())
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingNotExistItem))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllBookingForUserTest() throws Exception {
        when(bookingService.getAllBookings(anyInt(), anyString(), anyString(), anyString()))
                .thenThrow(BadRequestException.class);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "1")
                        .param("from", "1")
                        .param("size", "2")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
