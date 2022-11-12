package ru.practicum.shareit.exception;

public class InvalidStateBookingException extends RuntimeException {
    public InvalidStateBookingException(String s) {
        super(s);
    }
}
