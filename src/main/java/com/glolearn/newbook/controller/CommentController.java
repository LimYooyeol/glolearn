package com.glolearn.newbook.controller;

import com.glolearn.newbook.annotation.Auth;
import com.glolearn.newbook.aspect.auth.UserContext;
import com.glolearn.newbook.domain.Comment;
import com.glolearn.newbook.domain.Lecture;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.dto.comment.CommentDetailsDto;
import com.glolearn.newbook.dto.comment.CommentRegisterDto;
import com.glolearn.newbook.dto.comment.CommentUpdateDto;
import com.glolearn.newbook.dto.lecture.LecturePreviewDto;
import com.glolearn.newbook.exception.InvalidAccessException;
import com.glolearn.newbook.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;
    private final MemberService memberService;
    private final LectureService lectureService;
    private final EnrollmentService enrollmentService;

    // 댓글 목록 조회
    @GetMapping("/lecture/{lectureId}/comments")
    @Auth
    public String commentList(Model model,
                              @PathVariable("lectureId") Long lectureId){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {return "redirect:/login";}

        // 강의 조회
        Lecture lecture = lectureService.findById(lectureId);
        if(lecture == null) {throw new IllegalArgumentException("존재하지 않는 강의입니다.");}

        // 인가
        if(enrollmentService.findByMemberIdAndCourseId(member.getId(), lecture.getCourse().getId()) == null){
            if(lecture.getCourse().getLecturer().getId() != member.getId()){
                throw new InvalidAccessException("수강 후 조회할 수 있습니다.");
            }
        }

        List<CommentDetailsDto> comments = commentService.findAllByLectureId(lecture.getId());

        model.addAttribute("nickname", member.getNickname());
        model.addAttribute("lecture", new LecturePreviewDto(lecture));
        model.addAttribute("comments", comments);
        model.addAttribute("commentRegisterDto", new CommentRegisterDto());

        return "comment/list";
    }

    // 댓글 작성
    @PostMapping("/{lectureId}/comment")
    @Auth
    public String addComment(
            @PathVariable("lectureId") Long lectureId,
            @Valid CommentRegisterDto commentRegisterDto,
            BindingResult result
    ){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member == null) {return "redirect:/login";}

        // 강의 조회
        Lecture lecture = lectureService.findById(lectureId);
        if(lecture == null) {throw new IllegalArgumentException("존재하지 않는 강의입니다.");}

        // 인가
        if(enrollmentService.findByMemberIdAndCourseId(member.getId(), lecture.getCourse().getId()) == null){
            if(lecture.getCourse().getLecturer().getId() != member.getId()){
                throw new InvalidAccessException("수강 후 작성할 수 있습니다.");
            }
        }

        // 유효성 검사
        if(result.hasErrors()){
            return "redirect:/lecture/"+lectureId +"/comments";
        }

        commentService.addComment(member.getId(), lecture.getId(), commentRegisterDto.getRootId(), commentRegisterDto);
        return "redirect:/lecture/" + lectureId + "/comments";
    }

    @DeleteMapping("/comments/{commentId}")
    @Auth
    public String deleteComment(
            @PathVariable(name = "commentId") Long commentId
    ){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member==null){return "redirect:/login";}

        // 댓글 조회
        Comment comment = commentService.findById(commentId);
        if(comment == null) {throw new IllegalArgumentException("존재하지 않는 댓글입니다.");}

        // 인가
        if(comment.getWriter().getId() != member.getId()){
            throw new InvalidAccessException("삭제 권한이 없습니다.");
        }

        Long lectureId = comment.getLecture().getId();

        // 댓글 삭제
        commentService.removeComment(commentId);

        return "redirect:/lecture/" + lectureId + "/comments";
    }

    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    @Auth
    public String modifyComment(
            @PathVariable(name = "commentId") Long commentId,
            @Valid CommentUpdateDto commentUpdateDto,
            BindingResult result
    ){
        // 인증
        Member member = memberService.findMember(UserContext.getCurrentMember());
        if(member==null){return "redirect:/login";}

        // 댓글 조회
        Comment comment = commentService.findById(commentId);
        if(comment == null) {throw new IllegalArgumentException("존재하지 않는 댓글입니다.");}

        // 인가
        if(comment.getWriter().getId() != member.getId()){
            throw new InvalidAccessException("삭제 권한이 없습니다.");
        }

        // 유효성 검사
        if(result.hasErrors()){
            throw new IllegalArgumentException("내용을 입력해야 합니다.");
        }

        Long lectureId = comment.getLecture().getId();

        // 댓글 수정
        commentService.modifyComment(commentId, commentUpdateDto);

        return "redirect:/lecture/" + lectureId + "/comments";
    }


}
