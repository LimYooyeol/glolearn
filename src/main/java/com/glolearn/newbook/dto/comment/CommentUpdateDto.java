package com.glolearn.newbook.dto.comment;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentUpdateDto {
    @NotBlank
    private String contents;
}
