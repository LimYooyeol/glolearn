package com.glolearn.newbook.dto.member;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class NewNickname {

    @NotBlank
    @Size(min = 2, max = 50)
    String newNickname;
}
