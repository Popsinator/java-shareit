package ru.practicum.shareit.exception;

import lombok.Data;

@Data
public class ErrorResponse {

    String Error;

    public ErrorResponse(String error) {
        this.Error = error;
    }
}
