package ru.practicum.shareit.requests;

import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequest createItemRequest(ItemRequestDto requestDto, int userId);

    List<ItemRequest> getRequests(int userId);

    List<ItemRequest> getRequestWithPagination(int userId, int from, int size);

    ItemRequest getRequestListOnRequesterId(int requesterId, int userId);
}
