package com.glolearn.newbook.dto.comment;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentRegisterDto {
    @NotBlank
    private String contents;

    private Long rootId = null;
}
