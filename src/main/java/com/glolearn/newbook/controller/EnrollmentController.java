package com.glolearn.newbook.controller;

import com.glolearn.newbook.annotation.Auth;
import com.glolearn.newbook.aspect.auth.UserContext;
import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.exception.InvalidAccessException;
import com.glolearn.newbook.payment.TossPayments;
import com.glolearn.newbook.service.CourseService;
import com.glolearn.newbook.service.EnrollmentService;
import com.glolearn.newbook.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    private final MemberService memberService;
    private final TossPayments tossPayments;
    private final CourseService courseService;


    // 코스 수강 요청(결제 승인)
    @GetMapping("/enrollment/{courseId}")
    @Auth
    public String enrollCourse(
            @PathVariable(name = "courseId") Long courseId,
            @RequestParam(name = "amount") Long amount,
            @RequestParam(name = "orderId") String orderId,
            @RequestParam(name = "paymentKey") String paymentKey
    ){
        //인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {throw new InvalidAccessException("로그인 후 결제해주세요.");}

        //코스 조회
        Course course = courseService.findById(courseId);
        if(course == null) {throw new IllegalArgumentException("존재하지 않는 코스입니다.");}

        //유효성 검사
            // 클라이언트가 가격을 변경한 경우
        if(!course.getPrice().equals(amount)){
            throw new InvalidAccessException("코스의 가격과 결제 가격이 다릅니다.\n" +
                    "코스 가격 : " + course.getPrice() + "\n" +
                    "결제 가격 : " + amount);
        }
            // 이미 등록한 코스(중복결제 예방)
        if(enrollmentService.findByMemberIdAndCourseId(member.getId(), course.getId()) != null){
            throw new InvalidAccessException("이미 등록한 코스입니다.");
        }

        // 결제 승인 요청
        Boolean confirmResult = tossPayments.requireConfirm(paymentKey, orderId, amount);
        if(!confirmResult){
            return "redirect:/payment/failure";
        }

        //코스 등록
        enrollmentService.addEnrollment(member.getId(), course.getId(), orderId);

        return "redirect:/course/" + courseId;
    }

    // 결제 실패
    @GetMapping("/payment/failure")
    @Auth
    public String paymentFailure(
            Model model
    ){
        System.out.println("여기까지 ㄴ옴");

        //인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member != null) {model.addAttribute("nickname", member.getNickname());}

        //모델 전달
        model.addAttribute("errorMessage", "결제에 실패하였습니다. 다시 시도해주세요.");

        return "error/paymentFailure";
    }


    // 무료 코스 수강 신청
    @PostMapping("/enrollment/{courseId}/free")
    @Auth
    public String enrollCourse(
            @PathVariable(name = "courseId") Long courseId
    ){
        enrollmentService.addEnrollment(UserContext.getCurrentMember(), courseId, UUID.randomUUID().toString());

        return "redirect:/course/" + courseId;
    }
}
