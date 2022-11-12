package ru.practicum.shareit.exception;

public class NotBookingForUserException extends RuntimeException {
    public NotBookingForUserException(String s) {
        super(s);
    }
}
