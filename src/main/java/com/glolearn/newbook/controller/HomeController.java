package com.glolearn.newbook.controller;

import com.glolearn.newbook.annotation.Auth;
import com.glolearn.newbook.aspect.auth.UserContext;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.dto.course.CoursePreviewDto;
import com.glolearn.newbook.exception.InvalidAccessException;
import com.glolearn.newbook.oauth.exception.InvalidAccessTokenException;
import com.glolearn.newbook.service.CourseService;
import com.glolearn.newbook.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final MemberService memberService;
    private final CourseService courseService;

    @GetMapping("/")
    @Auth
    public String home(Model model){
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member != null) {model.addAttribute("nickname", member.getNickname());}


        List<CoursePreviewDto> popularCourses = courseService.findPopularCourseList();
        model.addAttribute("popularCourses", popularCourses);

        return "index";
    }
}
