package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class CommentDto {
    private Integer id;
    private String text;
    private String authorName;
    private String created;

    public CommentDto(Integer id, String text, String authorName, String created) {
        this.id = id;
        this.text = text;
        this.authorName = authorName;
        this.created = created;
    }
}
