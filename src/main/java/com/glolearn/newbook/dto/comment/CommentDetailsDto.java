package com.glolearn.newbook.dto.comment;

import com.glolearn.newbook.domain.Comment;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CommentDetailsDto {

    private Long id;

    private String writer;

    private LocalDate regDate;

    private String contents;

    private Boolean isReply;

    public CommentDetailsDto(Comment c) {
        this.id = c.getId();
        this.writer = c.getWriter().getNickname();
        this.regDate = c.getRegDate().toLocalDate();
        this.contents = c.getContents();
        this.isReply = (c.getId() != c.getRoot().getId());
    }
}
