package com.glolearn.newbook.dto.member;

import com.glolearn.newbook.domain.Auth.OauthDomain;
import com.glolearn.newbook.domain.Member;
import lombok.Data;

@Data
public class MemberInfoDto {

    private Long id;

    private String oauthId;

    private OauthDomain oauthDomain;

    private String nickname;

    public MemberInfoDto(Member member){
        this.id = member.getId();
        this.oauthId = member.getOauthId();
        this.oauthDomain = member.getOauthDomain();
        this.nickname = member.getNickname();
    }

}
