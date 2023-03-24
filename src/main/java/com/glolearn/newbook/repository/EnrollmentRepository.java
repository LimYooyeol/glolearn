package com.glolearn.newbook.repository;

import com.glolearn.newbook.domain.*;
import com.glolearn.newbook.dto.course.CourseSearchDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static com.glolearn.newbook.domain.QCourse.course;

@Repository
@RequiredArgsConstructor
public class EnrollmentRepository {
    private final EntityManager em;

    // 수강 추가
    public Long save(Enrollment enrollment){
        em.persist(enrollment);

        return enrollment.getId();
    }

    public Enrollment findById(Long id){
        return em.find(Enrollment.class, id);
    }

    // 수강 조회(memberId & courseId)
    public Enrollment findByMemberIdAndCourseId(Long memberId, Long courseId){
        String query = "select e from Enrollment e" +
                        " where e.member.id =:memberId " +
                        " and " +
                        " e.course.id =: courseId";

        Enrollment enrollment = (Enrollment) em.createQuery(query)
                .setParameter("memberId", memberId)
                .setParameter("courseId", courseId)
                .getResultList().stream().findFirst().orElse(null);

        return enrollment;
    }

}
