package ru.practicum.shareit.ItemsTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemsControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final Item item = new Item(1, "test", "Description test", true, null, null);

    private final CommentDto commentDto = new CommentDto(1, "test", "user", "");

    private final List<Item> listUsers = List.of(item);

    @Test
    void saveNewItemTest() throws Exception {
        when(itemService.createItem(any(), anyInt()))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId())))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.updateItem(any(), anyInt(), anyInt()))
                .thenReturn(item);

        mvc.perform(patch("/items/" + item.getId())
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId())))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @Test
    void getItemTest() throws Exception {
        when(itemService.getItem(anyInt(), anyInt()))
                .thenReturn(item);

        mvc.perform(get("/items/" + anyInt())
                        .header("X-Sharer-User-Id", anyInt())
                        .content(mapper.writeValueAsString(item))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId())))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));
    }

    @Test
    void getAllItemsTest() throws Exception {
        when(itemService.getAllItems(anyInt()))
                .thenReturn(listUsers);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", anyInt())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(listUsers.size())))
                .andExpect(jsonPath("[0].id", is(item.getId())))
                .andExpect(jsonPath("[0].name", is(item.getName())))
                .andExpect(jsonPath("[0].description", is(item.getDescription())))
                .andExpect(jsonPath("[0].available", is(item.getAvailable())));
    }

    @Test
    void getSearchItemsTest() throws Exception {
        when(itemService.findItemsOnDescription(anyString()))
                .thenReturn(listUsers);

        mvc.perform(get("/items/search")
                        .param("text", anyString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(listUsers.size())))
                .andExpect(jsonPath("[0].id", is(item.getId())))
                .andExpect(jsonPath("[0].name", is(item.getName())))
                .andExpect(jsonPath("[0].description", is(item.getDescription())))
                .andExpect(jsonPath("[0].available", is(item.getAvailable())));
    }

    @Test
    void getDeleteItemTest() throws Exception {
        mvc.perform(delete("/items/" + anyInt())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void saveNewCommentTest() throws Exception {
        when(itemService.createComment(any(), eq(1), eq(1)))
                .thenReturn(commentDto);

        mvc.perform(post("/items/" + 1 + "/comment")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId())))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(commentDto.getCreated())));
    }
}
