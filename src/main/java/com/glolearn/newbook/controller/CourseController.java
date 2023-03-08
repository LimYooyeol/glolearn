package com.glolearn.newbook.controller;

import com.glolearn.newbook.annotation.Auth;
import com.glolearn.newbook.aspect.auth.UserContext;
import com.glolearn.newbook.domain.Category;
import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.dto.course.CourseDetailsDto;
import com.glolearn.newbook.dto.course.CourseRegisterDto;
import com.glolearn.newbook.exception.InvalidAccessException;
import com.glolearn.newbook.service.CourseService;
import com.glolearn.newbook.service.MemberService;
import javassist.NotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final MemberService memberService;

    @GetMapping("/course/register")
    @Auth
    public String register(Model model){
        // 0. 비로그인 처리
        if(UserContext.getCurrentMember() == null){
            return "redirect:/login";
        }

        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {throw new InvalidAccessException("Token with non-existing member.");}

        // 1. model 에 데이터 전달
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("courseRegisterDto", new CourseRegisterDto());

        return "/course/registerForm";
    }

    @PostMapping("/course/register")
    @Auth
    public String register(@Valid CourseRegisterDto courseRegisterDto, BindingResult result, Model model){
        System.out.println(courseRegisterDto.toString());
        if(result.hasErrors()){
            Member member = memberService.findMember(UserContext.getCurrentMember());
            model.addAttribute("nickname", member.getNickname());

            return "/course/registerForm";
        }

        // 0. 비로그인 처리
        if(UserContext.getCurrentMember() == null) {return "redirect:/login";}
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null){throw new InvalidAccessException("Token with non-existing member.");}

        // 2. 등록
        courseService.addCourse(member.getId(), courseRegisterDto);
        return "redirect:/lecturer";
    }

    @GetMapping("/course/{id}")
    @Auth
    public String detail(@PathVariable(name = "id") Long courseId,
                         Model model){
        Member member;

        // 로그인 정보 처리
        if(UserContext.getCurrentMember() != null) {
            member = memberService.findMember(UserContext.getCurrentMember());
            if (member != null) {
                model.addAttribute("nickname", member.getNickname());
            }
        }

        Course course = courseService.findById(courseId);
        if(course == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        model.addAttribute("courseDetailsDto", new CourseDetailsDto(course));
        return "/course/details";
    }

    @GetMapping("/course/{category}")
    public String courseList(
            @PathVariable(name = "category", required = false) String category,
            @RequestParam(required = false, defaultValue = "") String sort,
            @RequestParam(name = "page") int page
    ){

        return "/course/list";
    }

//
//    @GetMapping("/course/{courseId}/{lectureId}")
//    @Auth
//    public String lectureDetails(
//            @PathVariable(name = "courseId") String courseId,
//            @PathVariable(name = "lectureId") String lectureId,
//            Model model
//    ){
//        // 0. 비로그인 처리
//        Member member = memberService.findMember(UserContext.getCurrentMember());
//        if(member != null){model.addAttribute("nickname", member.getNickname());}
//
//        return "/course/lecture/details";
//    }
//
//    @GetMapping("/course/list")
//    @Auth
//    public String list(){
//        // 0. 비로그인 처리
//        Member member = memberService.findMember(UserContext.getCurrentMember());
//        if(member == null){throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);}
//
//        return "/course/list";
//    }
//
//    @GetMapping("/lecturer")
//    @Auth
//    public String lecturer(Model model){
//        // 0. 비로그인 처리
//        Member member = memberService.findMember(UserContext.getCurrentMember());
//        if(member != null){model.addAttribute("nickname", member.getNickname());}
//
//        return "/lecturer/course/list";
//    }
//
//    @GetMapping("/lecturer/course/{id}")
//    @Auth
//    public String manageLecture(Model model,
//                                @PathVariable(name = "id") Long id){
//        // 0. 비로그인 처리
//        Member member = memberService.findMember(UserContext.getCurrentMember());
//        if(member != null){model.addAttribute("nickname", member.getNickname());}
//
//        return "/lecturer/course/manage";
//    }

}
