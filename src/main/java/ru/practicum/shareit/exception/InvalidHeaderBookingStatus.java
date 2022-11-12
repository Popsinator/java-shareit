package ru.practicum.shareit.exception;

public class InvalidHeaderBookingStatus extends RuntimeException {
    public InvalidHeaderBookingStatus(String s) {
        super(s);
    }
}
