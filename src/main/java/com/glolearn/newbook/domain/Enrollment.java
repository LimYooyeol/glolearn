package com.glolearn.newbook.domain;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
public class Enrollment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    private LocalDateTime enrollDate;

    private String orderId;

    protected Enrollment(){}
    public static Enrollment createEnrollment(Member member, Course course, String orderId) {
        Enrollment enrollment = new Enrollment();
        enrollment.member = member;
        enrollment.course = course;
        enrollment.course.addStudent();
        enrollment.enrollDate = LocalDateTime.now();
        enrollment.orderId = orderId;

        return enrollment;
    }
}
