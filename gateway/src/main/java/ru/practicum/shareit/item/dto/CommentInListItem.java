package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentInListItem {
    private Integer id;
    private String text;
    private String authorName;
    private String created;

    public CommentInListItem(Integer id, String text, String authorName, String created) {
        this.id = id;
        this.text = text;
        this.authorName = authorName;
        this.created = created;
    }
}
