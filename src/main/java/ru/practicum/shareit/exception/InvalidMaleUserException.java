package ru.practicum.shareit.exception;

import lombok.Data;

public class InvalidMaleUserException extends RuntimeException {

    String parameter;
    public InvalidMaleUserException(String s) {
        super(s);
        this.parameter = s;
    }
}