package ru.practicum.shareit.requests;

import lombok.Data;

import java.util.Date;

@Data
public class ItemRequest {

    private int id;
    private String description;
    private String requestor;
    private Date created;
}
