package ru.practicum.shareit.booking.dto;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class LastBooking {

    String lastBooking;

    Integer id;

    Integer bookerId;
}
