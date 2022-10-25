package ru.practicum.shareit.exception;

public class NotFoundOwnerItemException extends RuntimeException {

    String parameter;
    public NotFoundOwnerItemException(String s) {
        super(s);
        this.parameter = s;
    }
}
