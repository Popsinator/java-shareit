package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping()
    public Item create(@RequestHeader("X-Sharer-User-Id") int userId, @RequestBody Item item) {
        return itemService.createItem(item, userId);
    }

    @PostMapping(path = "/{itemId}/comment")
    public CommentDto addComments(@RequestHeader("X-Sharer-User-Id") int userId,
                                  @RequestBody Comment comment,
                                  @PathVariable String itemId) {
        return itemService.createComment(comment, userId, Integer.parseInt(itemId));
    }

    @PatchMapping(path = "/{itemId}")
    public Item update(@RequestHeader("X-Sharer-User-Id") Integer userId,
                       @RequestBody Item item, @PathVariable String itemId) {
        return itemService.updateItem(item, Integer.parseInt(itemId), userId);
    }

    @GetMapping(path = "/{itemId}")
    public Item getItemOnItemId(@PathVariable String itemId,
                                @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItem(Integer.parseInt(itemId), userId);
    }

    @GetMapping
    public Collection<Item> getAllItemOnUserId(@RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping(path = "/search")
    public Collection<Item> getAllItemOnText(@RequestParam String text) {
        return itemService.findItemsOnDescription(text);
    }

    @DeleteMapping(path = "/{itemId}")
    public void deleteUser(@PathVariable String itemId) {
        itemService.deleteItem(Integer.parseInt(itemId));
    }
}
