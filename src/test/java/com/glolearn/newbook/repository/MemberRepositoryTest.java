package com.glolearn.newbook.repository;

import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.domain.OAuthDomain;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberRepositoryTest {
    @Autowired MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void 회원삽입_테스트(){
        // given
        Member member = Member.createMember("asdf",OAuthDomain.KAKAO, "회원1");
        // when
        memberRepository.save(member);

        // then
        assertEquals(member, memberRepository.findById(member.getId()));
    }

    @Test
    @Transactional
    public void 회원검색테스트(){
        // given
        String oAuthId = "123";
        OAuthDomain oAuthDomain = OAuthDomain.KAKAO;

        // when
        Member member = memberRepository.findByOAuthIdAndOAuthDomain(oAuthId, oAuthDomain);

        // then
        assertNull(member);
    }
}