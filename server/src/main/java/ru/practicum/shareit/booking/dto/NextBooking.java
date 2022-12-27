package ru.practicum.shareit.booking.dto;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class NextBooking {

    String nextBooking;

    Integer id;

    Integer bookerId;
}
