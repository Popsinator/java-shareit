package ru.practicum.shareit.exception;

public class InvalidHeaderUserId extends RuntimeException {
    public InvalidHeaderUserId(String s) {
        super(s);
    }
}
