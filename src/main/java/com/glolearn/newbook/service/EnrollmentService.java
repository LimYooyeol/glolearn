package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.Enrollment;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.repository.CourseRepository;
import com.glolearn.newbook.repository.EnrollmentRepository;
import com.glolearn.newbook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public Long addEnrollment(Long memberId, Long courseId){
        Member member = memberRepository.findById(memberId);
        Course course = courseRepository.findById(courseId).orElse(null);

        if(member == null || course == null){
            throw new IllegalArgumentException("존재하지 않는 회원이나 코스입니다.");
        }

        Enrollment enrollment = Enrollment.createEnrollment(member, course);
        return enrollmentRepository.save(enrollment);
    }

    public Enrollment findById(Long id){
        return enrollmentRepository.findById(id);
    }

    // 수강 조회(memberId & courseId)
    public Enrollment findByMemberIdAndCourseId(Long memberId, Long courseId){

        return enrollmentRepository.findByMemberIdAndCourseId(memberId, courseId);
    }
}
