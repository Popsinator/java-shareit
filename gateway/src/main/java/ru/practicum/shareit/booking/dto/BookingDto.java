package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.Marker;
import ru.practicum.shareit.booking.Status;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    private int id;

    @NotNull(groups = Marker.OnCreate.class)
    private LocalDateTime start;

    @NotNull(groups = Marker.OnCreate.class)
    private LocalDateTime end;

    private int itemId;

    private Status status;
}
