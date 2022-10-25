package ru.practicum.shareit.exception;

public class IdUserNotExistException extends RuntimeException {
    public IdUserNotExistException(String s) {
        super(s);
    }
}
