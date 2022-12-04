package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;

public class RequestMapper {

    public static ItemRequest toItemRequestDto(ItemRequestDto request, User user) {
        return new ItemRequest(
                0,
                request.getDescription() == null ? null : request.getDescription(),
                user,
                request.getCreated() == null ? null : request.getCreated(),
                request.getItems() == null ? null : request.getItems()
        );
    }
}
