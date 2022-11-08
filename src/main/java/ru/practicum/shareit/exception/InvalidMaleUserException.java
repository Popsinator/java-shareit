package ru.practicum.shareit.exception;

public class InvalidMaleUserException extends RuntimeException {

    private String parameter;

    public InvalidMaleUserException(String s) {
        super(s);
        this.parameter = s;
    }
}