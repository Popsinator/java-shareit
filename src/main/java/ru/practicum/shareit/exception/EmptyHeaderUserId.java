package ru.practicum.shareit.exception;

public class EmptyHeaderUserId extends RuntimeException {
    public EmptyHeaderUserId(String s) {
        super(s);
    }
}
