package com.glolearn.newbook.controller;

import com.glolearn.newbook.annotation.Auth;
import com.glolearn.newbook.aspect.auth.UserContext;
import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.Lecture;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.dto.lecture.LectureDetailsDto;
import com.glolearn.newbook.dto.lecture.LectureRegisterDto;
import com.glolearn.newbook.dto.lecture.LectureUpdateDto;
import com.glolearn.newbook.exception.InvalidAccessException;
import com.glolearn.newbook.service.CourseService;
import com.glolearn.newbook.service.EnrollmentService;
import com.glolearn.newbook.service.LectureService;
import com.glolearn.newbook.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class LectureController {
    private final MemberService memberService;
    private final CourseService courseService;
    private final LectureService lectureService;

    private final EnrollmentService enrollmentService;

    // 강의 조회(일반 회원)
    @GetMapping("/lecture/{lectureId}")
    @Auth
    public String getLecture(
            @PathVariable(name = "lectureId") Long lectureId,
            Model model
    ){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {return "redirect:/login";}

        // 강의 조회
        Lecture lecture = lectureService.findById(lectureId);
        if(lecture == null){throw new ResponseStatusException(HttpStatus.NOT_FOUND);}
        LectureDetailsDto lectureDetailsDto = new LectureDetailsDto(lecture);

        Long courseId = lecture.getCourse().getId();

        // 인가
        if(enrollmentService.findByMemberIdAndCourseId(member.getId(), lecture.getCourse().getId()) == null){
            if(lectureService.findFirstLecture(courseId).getId() != lecture.getId() &&
               lecture.getCourse().getLecturer().getId() != member.getId()){
                throw new InvalidAccessException("수강 신청을 먼저 해야합니다.");
            }
        }

        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("lectureDetailsDto", lectureDetailsDto);
        return "lecture/details";
    }

    // 강의 단건 조회(강사)
    @GetMapping("/lecture/{lectureId}/manage")
    @Auth
    public String lectureDetailPage(
            @PathVariable(name = "lectureId") Long lectureId,
            Model model
    ){
        //인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {return "redirect:/login";}

        //강의 조회
        Lecture lecture = lectureService.findById(lectureId);
        if(lecture == null) {throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        //인가
        if(lecture.getCourse().getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("접근 권한이 없습니다.");
        }

        // 데이터 전송
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("lecture", new LectureDetailsDto(lecture));

        return "/lecture/manage";
    }


    // 강의 등록 페이지
    @GetMapping("/course/{courseId}/lecture/register")
    @Auth
    public String lectureRegisterPage(
            @PathVariable(name = "courseId") Long courseId,
            Model model
    ){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null){ return "redirect:/login";}

        // 코스 조회
        Course course = courseService.findById(courseId);
        if(course == null) {throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        //인가
        if(member.getId() != course.getLecturer().getId()){throw new InvalidAccessException("작성 권한이 없습니다.");}

        // 데이터 전달
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("courseId", courseId);
        model.addAttribute("lectureRegisterDto", new LectureRegisterDto());

        return "/lecture/registerForm";
    }


    // 강의 등록
    @PostMapping("/course/{courseId}/lecture")
    @Auth
    public String registerLecture(
            @PathVariable(name = "courseId") Long courseId,
            @Valid LectureRegisterDto lectureRegisterDto,
            BindingResult result,
            Model model
    ){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {throw new InvalidAccessException("로그인 후 등록이 가능합니다.");}

        // 유효성 검사
        if(result.hasErrors()){
            model.addAttribute("courseId", courseId);
            model.addAttribute("nickname", member.getNickname());

            return "/lecture/registerForm";
        }

        // 인가
        Course course = courseService.findById(courseId);
        if(course == null) {throw new ResponseStatusException(HttpStatus.NOT_FOUND);}
        if(course.getLecturer().getId() != member.getId()) {throw new InvalidAccessException("작성 권한이 없습니다.");}

        // 등록
        Long lectureId = lectureService.addLecture(courseId, lectureRegisterDto);

        return "redirect:/lecture/" + lectureId + "/manage";
    }

    //강의 수정 페이지
    @GetMapping("/lecture/{lectureId}/modify")
    @Auth
    public String lectureModifyPage(
            @PathVariable(name = "lectureId") Long lectureId,
            Model model
    ){

        //인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) { return "redirect:/login";}

        //강의 조회
        Lecture lecture = lectureService.findById(lectureId);
        if(lecture == null) {throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        //인가
        if(lecture.getCourse().getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("수정 권한이 없습니다.");
        }

        //모델 전송
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("lectureId", lectureId);
        model.addAttribute("lectureUpdateDto", new LectureUpdateDto(lecture));

        return "/lecture/modifyForm";
    }

    // 강의 수정
    @PutMapping("/lecture/{lectureId}")
    @Auth
    public String modifyLecture(
            @PathVariable(name = "lectureId") Long lectureId,
            @Valid LectureUpdateDto lectureUpdateDto,
            BindingResult result,
            Model model
    ){
        //인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) { return "redirect:/login";}

        //유효성 검사
        if(result.hasErrors()){
            model.addAttribute("nickname", member.getNickname());
            model.addAttribute("lectureId", lectureId);
            return "/course/lecture/modifyForm";
        }

        //강의 조회
        Lecture findLecture = lectureService.findById(lectureId);
        if(findLecture == null) {throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        //인가
        if(findLecture.getCourse().getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("수정 권한이 없습니다.");
        }

        //수정
        lectureService.modifyLecture(lectureId, lectureUpdateDto);

        return "redirect:/lecture/" + lectureId + "/manage";
    }

    // 강의 삭제
    @DeleteMapping("/lecture/{lectureId}")
    @Auth
    public String deleteLecture(
            @PathVariable(name = "lectureId") Long lectureId
    ){
        //인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) { return "redirect:/login";}

        //강의 조회
        Lecture lecture = lectureService.findById(lectureId);
        if(lecture == null) {throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        //인가
        if(lecture.getCourse().getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("삭제 권한이 없습니다.");
        }

        //삭제
        lectureService.removeById(lectureId);

        return "redirect:/course/" + lecture.getCourse().getId() + "/manage";
    }


}
