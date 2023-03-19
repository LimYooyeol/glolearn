package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Auth.OauthDomain;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.exception.InvalidAccessException;
import com.glolearn.newbook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;

    // 회원 추가
    @Transactional
    public void addMember(Member member){
        memberRepository.save(member);
    }

    // 회원 조회(ID)
    public Member findMember(Long id) {
        if(id == null){
            return null;
        }

        return memberRepository.findById(id);
    }

    // 회원 조회(OAuth Id && OAuth Domain)
    public Member findByOauthIdAndOauthDomain(String oauthId, OauthDomain oauthDomain) {
        return memberRepository.findByOauthIdAndOauthDomain(oauthId, oauthDomain);
    }

    @Transactional
    public void modifyNickname(Long id, String newNickname) {
        Member member = memberRepository.findById(id);
        if(member == null){throw new InvalidAccessException("존재하지 않는 회원의 이름을 변경할 수 없습니다.");}

        member.updateNickname(newNickname);
    }
}
