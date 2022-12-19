package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping()
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody Item item) {
        return itemClient.createItem(item, userId);
    }

    @PostMapping(path = "/{itemId}/comment")
    public ResponseEntity<Object> addComments(@RequestHeader("X-Sharer-User-Id") int userId,
                                  @RequestBody Comment comment,
                                  @PathVariable String itemId) {
        return itemClient.createComment(comment, userId, Integer.parseInt(itemId));
    }

    @PatchMapping(path = "/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") Integer userId,
                       @RequestBody Item item, @PathVariable String itemId) {
        return itemClient.updateItem(item, Integer.parseInt(itemId), userId);
    }

    @GetMapping(path = "/{itemId}")
    public ResponseEntity<Object> getItemOnItemId(@PathVariable String itemId,
                                @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.getItem(Integer.parseInt(itemId), userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemOnUserId(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemClient.getAllItems(userId);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<Object> getAllItemOnText(@RequestParam String text) {
        return itemClient.findItemsOnDescription(text);
    }

    @DeleteMapping(path = "/{itemId}")
    public void deleteUser(@PathVariable String itemId) {
        itemClient.deleteItem(Integer.parseInt(itemId));
    }
}
