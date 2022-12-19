package ru.practicum.shareit.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.requests.dto.ItemRequestDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final RequestClient requestClient;

    @PostMapping()
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody ItemRequestDto request) {
        return requestClient.createItemRequest(request, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return requestClient.getRequests(userId);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Object> getAllRequestsWithoutPaginationParams(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                                   @RequestParam(required = false) String from,
                                                                   @RequestParam(required = false) String size) {
        if (from == null || size == null) {
            return requestClient.getRequests(userId);
        } else {
            return requestClient.getRequestWithPagination(userId, Integer.parseInt(from), Integer.parseInt(size));
        }
    }

    @GetMapping(path = "/{requesterId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Integer userId,
                           @PathVariable String requesterId) {
        return requestClient.getRequestListOnRequesterId(Integer.parseInt(requesterId), userId);
    }
}
