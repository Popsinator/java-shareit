package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> createItem(Item item, int userId) {
        return post(userId, item);
    }

    public ResponseEntity<Object> createComment(Comment comment, int userId, int itemId) {
        return post("/" + itemId + "/comment", userId, comment);
    }

    public ResponseEntity<Object> updateItem(Item item, int itemId, int userId) {
        return patch("/" + itemId, userId, item);
    }

    public ResponseEntity<Object> getItem(int itemId, int userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAllItems(int userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> findItemsOnDescription(String text) {
        Map<String, Object> parameters = Map.of(
                "text", text
        );
        return get("/search/" + "?text={text}", parameters);
    }

    public void deleteItem(int itemId) {
        delete("/" + itemId);
    }
}
