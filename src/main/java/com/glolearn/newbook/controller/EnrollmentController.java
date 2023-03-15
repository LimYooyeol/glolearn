package com.glolearn.newbook.controller;

import com.glolearn.newbook.annotation.Auth;
import com.glolearn.newbook.aspect.auth.UserContext;
import com.glolearn.newbook.domain.Enrollment;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.service.EnrollmentService;
import com.glolearn.newbook.service.LectureService;
import com.glolearn.newbook.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    private final MemberService memberService;

    // 수강 신청
    @PostMapping("/enrollment")
    @Auth
    public String enrollCourse(
            Long courseId
    ){
        enrollmentService.addEnrollment(UserContext.getCurrentMember(), courseId);

        return "redirect:/course/" + courseId;
    }
}
