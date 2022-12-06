package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

import java.util.List;

@RestController

@RequestMapping(path = "/requests")

@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService requestService;

    @PostMapping()
    public ItemRequest create(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody ItemRequestDto request) {
        return requestService.createItemRequest(request, userId);
    }

    @GetMapping()
    public List<ItemRequest> get(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return requestService.getRequests(userId);
    }

    @GetMapping(path = "/all")
    public List<ItemRequest> getAllRequestsWithoutPaginationParams(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                                   @RequestParam(required = false) String from,
                                                                   @RequestParam(required = false) String size) {
        if (from == null || size == null) {
            return requestService.getRequests(userId);
        } else {
            return requestService.getRequestWithPagination(userId, Integer.parseInt(from), Integer.parseInt(size));
        }
    }

    @GetMapping(path = "/{requesterId}")
    public ItemRequest get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                           @PathVariable String requesterId) {
        return requestService.getRequestListOnRequesterId(Integer.parseInt(requesterId), userId);
    }
}
