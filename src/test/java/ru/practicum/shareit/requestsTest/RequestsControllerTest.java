package ru.practicum.shareit.requestsTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.requests.ItemRequest;
import ru.practicum.shareit.requests.ItemRequestController;
import ru.practicum.shareit.requests.ItemRequestService;
import ru.practicum.shareit.user.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class RequestsControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private final User user = new User(1, "user", "user@user");

    private final ItemRequest itemRequest = new ItemRequest(1, "test", user, LocalDateTime.now(), null);

    private final List<ItemRequest> listItemRequest = List.of(itemRequest);

    @Test
    void saveItemRequstTest() throws Exception {
        when(itemRequestService.createItemRequest(any(), anyInt()))
                .thenReturn(itemRequest);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId())))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())));
    }

    @Test
    void getItemRequestTest() throws Exception {
        when(itemRequestService.getRequestListOnRequesterId(anyInt(), anyInt()))
                .thenReturn(itemRequest);

        mvc.perform(get("/requests/" + anyInt())
                        .header("X-Sharer-User-Id", anyInt())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequest.getId())))
                .andExpect(jsonPath("$.description", is(itemRequest.getDescription())));
    }

    @Test
    void getAllItemRequestTest() throws Exception {
        when(itemRequestService.getRequest(anyInt()))
                .thenReturn(listItemRequest);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(listItemRequest.size())))
                .andExpect(jsonPath("[0].id", is(listItemRequest.get(0).getId())))
                .andExpect(jsonPath("[0].description", is(listItemRequest.get(0).getDescription())));
    }

    @Test
    void getAllItemRequestWithPaginationTest() throws Exception {
        when(itemRequestService.getRequestWithPagination(anyInt(), anyInt(), anyInt()))
                .thenReturn(listItemRequest);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "1")
                        .param("size", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(listItemRequest.size())))
                .andExpect(jsonPath("[0].id", is(listItemRequest.get(0).getId())))
                .andExpect(jsonPath("[0].description", is(listItemRequest.get(0).getDescription())));
    }

    @Test
    void getAllItemRequestWithoutPaginationTest() throws Exception {
        when(itemRequestService.getRequest(anyInt()))
                .thenReturn(listItemRequest);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(listItemRequest.size())))
                .andExpect(jsonPath("[0].id", is(listItemRequest.get(0).getId())))
                .andExpect(jsonPath("[0].description", is(listItemRequest.get(0).getDescription())));
    }
}
