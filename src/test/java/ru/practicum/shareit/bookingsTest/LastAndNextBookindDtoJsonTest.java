package ru.practicum.shareit.bookingsTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.LastBooking;
import ru.practicum.shareit.booking.dto.NextBooking;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class LastAndNextBookindDtoJsonTest {

    @Autowired
    private JacksonTester<LastBooking> jsonLast;

    @Autowired
    private JacksonTester<NextBooking> jsonNext;

    @Test
    void testLastBooking() throws Exception {
        LastBooking booking = new LastBooking("test", 1, 1);

        JsonContent<LastBooking> result = jsonLast.write(booking);

        assertThat(result).extractingJsonPathStringValue("$.lastBooking").isEqualTo(booking.getLastBooking());
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(booking.getId());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(booking.getBookerId());
    }

    @Test
    void testNextBooking() throws Exception {
        NextBooking booking = new NextBooking("test", 1, 1);

        JsonContent<NextBooking> result = jsonNext.write(booking);

        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isEqualTo(booking.getNextBooking());
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(booking.getId());
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(booking.getBookerId());
    }
}
