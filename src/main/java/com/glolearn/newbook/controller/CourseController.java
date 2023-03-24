package com.glolearn.newbook.controller;

import com.glolearn.newbook.annotation.Auth;
import com.glolearn.newbook.aspect.auth.UserContext;
import com.glolearn.newbook.domain.*;
import com.glolearn.newbook.dto.course.*;
import com.glolearn.newbook.dto.lecture.LecturePreviewDto;
import com.glolearn.newbook.exception.InvalidAccessException;
import com.glolearn.newbook.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final MemberService memberService;
    private final LectureService lectureService;
    private final EnrollmentService enrollmentService;

    private final LastLectureHistoryService lastLectureHistoryService;

    @Value("${ip-address}")
    private String ipAddress;

    // 코스 등록 페이지(강사)
    @GetMapping("/course/register")
    @Auth
    public String register(Model model){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {return "redirect:/login";}

        // 모델 전달
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("courseRegisterDto", new CourseRegisterDto());

        return "course/registerForm";
    }

    // 코스 등록
    @PostMapping("/course")
    @Auth
    public String register(
            @Valid CourseRegisterDto courseRegisterDto,
            BindingResult result,
            Model model){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {return "redirect:/login";}

        // 유효성 검사
        if(result.hasErrors()){
            model.addAttribute("nickname", member.getNickname());
            return "course/registerForm";
        }

        // 코스 등록
        Long courseId = courseService.addCourse(member.getId(), courseRegisterDto);

        return "redirect:/course/" + courseId + "/manage";
    }

    // 코스 단건 조회(회원)
    @GetMapping("/course/{id}")
    @Auth
    public String detail(@PathVariable(name = "id") Long courseId,
                         Model model){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member != null) {model.addAttribute("nickname", member.getNickname());}

        // 코스 조회
        Course course = courseService.findById(courseId);
        if(course == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        List<LecturePreviewDto> lectures = lectureService.findAllByCourseId(course.getId());

        // 등록여부 추가
        if(member == null){
            model.addAttribute("enrolled", false);
        }else{
            Enrollment enrollment = enrollmentService.findByMemberIdAndCourseId(member.getId(), course.getId());
            model.addAttribute("enrolled", enrollment != null);
        }

        // 모델 전달
        model.addAttribute("ipAddress", ipAddress);
        model.addAttribute("courseDetailsDto", new CourseDetailsDto(course));
        model.addAttribute("lectures", lectures);
        model.addAttribute("orderId", UUID.randomUUID());
        return "course/details";
    }

    @GetMapping("/course")
    public String initialCourseList(){
        return "redirect:/course/all";
    }

    // 전체 코스 조회(회원)
    @GetMapping("/courses/{category}")
    @Auth
    public String courseList(
            @PathVariable(name = "category", required = false) String category,
            @RequestParam(required = false, defaultValue = "recent") String sort,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "search", required = false) String search,
            Model model
    ){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member != null) {model.addAttribute("nickname", member.getNickname());}

        // 검색 조건 DTO
        CourseSearchDto courseSearchDto = new CourseSearchDto();
        courseSearchDto.setCategory(Category.of(category));
        courseSearchDto.setSort(Sort.of(sort));
        courseSearchDto.setPageNum(page);
        courseSearchDto.setSearch(search);

        courseSearchDto.setPageSize(6);

        // 검색
        int maxPage = courseService.findMaxPage(courseSearchDto);
        if(page < 1 || page > maxPage){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        // 코스 목록 조회
        List<CoursePreviewDto> courses = courseService.findCourses(courseSearchDto);

        // 모델 전달
        model.addAttribute("maxPage", maxPage);
        model.addAttribute("courseSearchDto", courseSearchDto);
        model.addAttribute("pagingBase", "/courses");
        model.addAttribute("courses", courses);
        return "course/list";
    }

    // 코스 수정(강사)
    @PutMapping("/course/{courseId}")
    @Auth
    public String updateCourse(
            @PathVariable(name = "courseId") Long courseId,
            @Valid CourseUpdateDto courseUpdateDto,
            BindingResult result,
            Model model){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {return "redirect:/login";}

        //유효성 검사
        if(result.hasErrors()){
            model.addAttribute("nickname", member.getNickname());
            return "course/modifyForm";
        }

        // 코스 조회
        Course course = courseService.findById(courseId);
        if(course == null) { throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        // 인가
        if(course.getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("수정 권한이 없습니다.");
        }

        // 코스 수정
        courseService.modifyCourse(courseId, courseUpdateDto);
        return "redirect:/course/" + courseId + "/manage";
    }


    // 코스 삭제(강사)
    @DeleteMapping("/course/{courseId}")
    @Auth
    public String deleteCourse(
            @PathVariable(name = "courseId")Long courseId
    ){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {return "redirect:/login";}

        // 코스 조회
        Course course = courseService.findById(courseId);
        if(course == null) { throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        // 인가
        if(course.getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("삭제 권한이 없습니다.");
        }

        // 코스 삭제
        courseService.removeById(courseId);
        return "redirect:/courses/mine/all";
    }

    // 코스 단건 조회(강사)
    @GetMapping("/course/{courseId}/manage")
    @Auth
    public String manageLecture(@PathVariable(name = "courseId") Long courseId,
                                Model model){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {return "redirect:/login";}

        // 코스 조회
        Course course = courseService.findById(courseId);
        if(course == null) { throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        // 인가
        if(course.getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("관리 권한이 없습니다.");
        }

        List<LecturePreviewDto> lectures = lectureService.findAllByCourseId(course.getId());
        CourseDetailsDto courseDetailsDto = new CourseDetailsDto(course);

        // 모델 전달
        model.addAttribute("lecturer", true);
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("course", courseDetailsDto);
        model.addAttribute("lectures", lectures);

        return "course/manage";
    }

    // 강의 중인 코스 목록 조회(강사)
    @GetMapping("/courses/mine/{category}")
    @Auth
    public String lecturer(
            @PathVariable(name = "category", required = false) String category,
            @RequestParam(required = false, defaultValue = "recent") String sort,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "search", required = false) String search,
            Model model
    ){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {return "redirect:/login";}

        // 검색 조건 DTO
        CourseSearchDto courseSearchDto = new CourseSearchDto();
        courseSearchDto.setCategory(Category.of(category));
        courseSearchDto.setSort(Sort.of(sort));
        courseSearchDto.setPageNum(page);
        courseSearchDto.setSearch(search);

        courseSearchDto.setPageSize(6);

        // 검색
        int maxPage = courseService.findMaxPageByLecturer(member.getId(), courseSearchDto);
        if(page < 1 || page > maxPage){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        List<CoursePreviewDto> courses = courseService.findCoursesByLecturer(member.getId(), courseSearchDto);

        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("courses", courses);
        model.addAttribute("maxPage", maxPage);
        model.addAttribute("courseSearchDto", courseSearchDto);
        model.addAttribute("pagingBase", "/courses/mine");
        model.addAttribute("lecturer", true);

        return "course/list";
    }

    // 코스 수정 페이지(강사)
    @GetMapping("/course/{courseId}/modify")
    @Auth
    public String updateCoursePage(
            @PathVariable("courseId") Long courseId,
            Model model
    ){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {return "redirect:/login";}

        // 코스 조회
        Course course = courseService.findById(courseId);
        if(course == null) { throw new ResponseStatusException(HttpStatus.NOT_FOUND);}

        // 인가
        if(course.getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("수정 권한이 없습니다.");
        }

        // 모델 전달

        CourseUpdateDto courseUpdateDto = new CourseUpdateDto(course);
        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("courseUpdateDto", courseUpdateDto);

        return "course/modifyForm";
    }

    // 코스 출시
    @PostMapping("/course/publish")
    @Auth
    public String publishCourse(Long courseId, Long price){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {return "redirect:/login";}

        // 코스 조회
        Course course = courseService.findById(courseId);
        if(course == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        //유효성 검사
        if(price != 0 && price < 1000){
            throw new IllegalArgumentException("가격은 1000원 이상이어야 합니다.");
        }

        // 인가
        if(course.getLecturer().getId() != member.getId()){
            throw new InvalidAccessException("수정 권한이 없습니다.");
        }

        courseService.publishCourse(courseId, price);
        return "redirect:/course/" + courseId + "/manage";
    }


    // 수강 중인 코스 조회
    @GetMapping("/courses/enroll/{category}")
    @Auth
    public String enrolledCourseList(
            @PathVariable(name = "category") String category,
            @RequestParam(required = false, defaultValue = "recent") String sort,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "search", required = false) String search,
            Model model
    ){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {return "redirect:/login";}

        // 검색 조건 DTO
        CourseSearchDto courseSearchDto = new CourseSearchDto();
        courseSearchDto.setCategory(Category.of(category));
        courseSearchDto.setSort(Sort.of(sort));
        courseSearchDto.setPageNum(page);
        courseSearchDto.setSearch(search);

        courseSearchDto.setPageSize(6);

        // 검색
        int maxPage = courseService.findMaxPageByMember(member.getId(), courseSearchDto);
        if(page < 1 || page > maxPage){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        List<CoursePreviewDto> courses = courseService.findCoursesByMember(member.getId(), courseSearchDto);

        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("courses", courses);
        model.addAttribute("maxPage", maxPage);
        model.addAttribute("courseSearchDto", courseSearchDto);
        model.addAttribute("pagingBase", "/courses/enroll");

        return "course/list";
    }

    //이어보기
    @GetMapping("/course/{courseId}/lecture/continue")
    @Auth
    public String lastViewedLecture(
            @PathVariable(name = "courseId") Long courseId
    ){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {return "redirect:/login";}

        // 기록 조회
        Long lastLecture = lastLectureHistoryService.findByMemberIdAndCourseId(member.getId(), courseId);

        // 기록이 없는 경우 - 코스의 첫번째 강의로
        if(lastLecture == null){
            Lecture firstLecture = lectureService.findFirstLecture(courseId);
            return "redirect:/lecture/" + firstLecture.getId();
        }

        return "redirect:/lecture/" + lastLecture;
    }


}
