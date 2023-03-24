package com.glolearn.newbook.controller;

import com.glolearn.newbook.annotation.Auth;
import com.glolearn.newbook.aspect.auth.UserContext;
import com.glolearn.newbook.domain.Auth.OauthDomain;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.dto.member.MemberInfoDto;
import com.glolearn.newbook.dto.member.NewNickname;
import com.glolearn.newbook.exception.InvalidAccessException;
import com.glolearn.newbook.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    // 마이페이지
    @GetMapping("/mypage")
    @Auth
    public String myPage(Model model){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {return "redirect:/login";}

        // DTO 생성
        MemberInfoDto memberInfoDto = new MemberInfoDto(member);

        // 모델 전달
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("memberInfoDto", memberInfoDto);
        return "member/myPage";
    }

    // 닉네임 변경 요청
    @PutMapping("/member/me/nickname")
    @ResponseBody
    @Auth
    public String changeNickname(
            @Valid
            NewNickname newNickname,
            BindingResult result,
            Model model
    ){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) { throw new InvalidAccessException("로그인 후 이용 가능합니다.");}

        // 유효성 검사
        if(result.hasErrors()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("changed", false);
            jsonObject.put("reason", "2자에서 50자 이내의 닉네임을 입력해주세요.");
            return jsonObject.toString();
        }

        // 닉네임 변경
        try{
            memberService.modifyNickname(member.getId(), newNickname.getNewNickname());
        }catch (Exception e){
            // ConstraintViolationException
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("changed", false);
            jsonObject.put("reason", "이미 존재하는 닉네임입니다.");
            return jsonObject.toString();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("changed", true);

        return jsonObject.toString();
    }

    // 회원 탈퇴
    @DeleteMapping("/member/me")
    @Auth
    public String dropMember(){
        //인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {throw new InvalidAccessException("존재하지 않는 회원입니다.");}

        //탈퇴
        memberService.removeById(member.getId());

        return "redirect:/";
    }

}
