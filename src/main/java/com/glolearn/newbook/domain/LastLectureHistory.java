package com.glolearn.newbook.domain;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class LastLectureHistory {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    protected LastLectureHistory() {}

    public static LastLectureHistory createLastLectureHistory(Member member, Course course, Lecture lecture) {
        LastLectureHistory history = new LastLectureHistory();
        history.member = member;
        history.course = course;
        history.lecture = lecture;

        return history;
    }

    public void update(Lecture lecture) {
        this.lecture = lecture;
    }
}
