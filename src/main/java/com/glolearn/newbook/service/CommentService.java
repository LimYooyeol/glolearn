package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Comment;
import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.Lecture;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.dto.comment.CommentDetailsDto;
import com.glolearn.newbook.dto.comment.CommentRegisterDto;
import com.glolearn.newbook.dto.comment.CommentUpdateDto;
import com.glolearn.newbook.dto.lecture.LectureRegisterDto;
import com.glolearn.newbook.repository.CommentRepository;
import com.glolearn.newbook.repository.LectureRepository;
import com.glolearn.newbook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final LectureRepository lectureRepository;

    // 댓글 작성(대댓글 작성 포함)
    @Transactional
    public Long addComment(
            Long memberId,
            Long lectureId,
            Long rootId,
            CommentRegisterDto commentRegisterDto)
    {
        // 회원 조회
        Member member = memberRepository.findById(memberId);
        if(member == null) {throw new IllegalArgumentException("존재하지 않는 회원입니다.");}

        // 강의 조회
        Lecture lecture = lectureRepository.findById(lectureId);
        if(lecture == null) {throw new IllegalArgumentException("존재하지 않는 강의입니다.");}

        // 원본 댓글 조회
        Comment root = null;
        if(rootId != null){
            root = commentRepository.findById(rootId);
            if(root == null) { throw new IllegalArgumentException("존재하지 않는 원댓글입니다.");}
        }

        // 댓글 추가
        Comment comment = Comment.createComment(member, lecture, root.getRoot(), commentRegisterDto);
        commentRepository.save(comment);

        // 대댓글이 아닌 경우 root 를 자기 자신으로
        if(comment.getRoot() == null){
            comment.updateRoot(comment);
        }

        return comment.getId();
    }

    // 댓글 수정
    @Transactional
    public Long modifyComment(Long commentId, CommentUpdateDto commentUpdateDto){
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId);
        if(comment == null) {throw new IllegalArgumentException("존재하지 않는 댓글입니다.");}

        // 댓글 수정
        comment.modify(commentUpdateDto);

        return comment.getId();
    }

    // 댓글 삭제
    @Transactional
    public void removeComment(Long commentId){
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId);
        if(comment == null) {throw new IllegalArgumentException("존재하지 않는 댓글입니다.");}

        // 댓글 삭제
        commentRepository.delete(comment);
    }

    // 댓글 단건 조회
    public Comment findById(Long id){
        return commentRepository.findById(id);
    }

    // 댓글 목록 조회
    public List<CommentDetailsDto> findAllByLectureId(Long lectureId){
        List<Comment> comments = commentRepository.findAllByLectureId(lectureId);

        List<CommentDetailsDto> result = comments.stream()
                .map(c -> new CommentDetailsDto(c)).collect(Collectors.toList());

        return result;
    }

}
