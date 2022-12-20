package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllBookingsWithoutParametersAndPagination(String path, int userId) {
        return get(path, userId, null);
    }

    public ResponseEntity<Object> getAllBookingsWithoutPagination(String path, int userId, String state) {
        Map<String, Object> parameters = Map.of(
                "state", state
        );
        return get(path + "?state={state}", userId, parameters);
    }

    public ResponseEntity<Object> getAllBookingsWithPagination(String path, int userId, String from, String size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get(path + "?from={from}&size={size}", userId, parameters);
    }

    /*public ResponseEntity<Object> getAllBookingsOwner(int userId, String state, String from, String size) {
        Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size
        );
        return get("/owner" + "?state={state}&from={from}&size={size}", userId, parameters);
    }*/


    public ResponseEntity<Object> createBooking(int userId, BookingDtoIn booking) {
        return post(userId, booking);
    }

    public ResponseEntity<Object> changeStatusOnApprovedOrRejected(int bookingId, int userId, String approved) {
        Map<String, Object> parameters = Map.of(
                "approved", approved
        );
        return patch("/" + bookingId + "?approved={approved}", userId, parameters);
    }

    /*public ResponseEntity<Object> getBooking(int userId, Long bookingId) {
        return get("/" + bookingId, userId);
    }*/

    public ResponseEntity<Object> getBookingDto(int bookingId, int userId) {
        return get("/" + bookingId, userId);
    }
}
