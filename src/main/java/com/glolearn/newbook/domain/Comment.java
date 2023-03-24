package com.glolearn.newbook.domain;

import com.glolearn.newbook.dto.comment.CommentRegisterDto;
import com.glolearn.newbook.dto.comment.CommentUpdateDto;
import com.glolearn.newbook.dto.course.CourseUpdateDto;
import lombok.Getter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Getter
public class Comment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    private LocalDateTime regDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "root_id")
    private Comment root;

    String contents;

    public static Comment createComment(Member member,
                                        Lecture lecture,
                                        Comment root,
                                        CommentRegisterDto commentRegisterDto)
    {
        Comment comment = new Comment();
        comment.writer = member;
        comment.lecture = lecture;
        comment.root = root;
        comment.contents = commentRegisterDto.getContents();

        comment.regDate = LocalDateTime.now();

        return comment;
    }

    public void updateRoot(Comment root){
        this.root = root;
    }

    public void modify(CommentUpdateDto commentUpdateDto){
        this.contents = commentUpdateDto.getContents();
    }


}
