package com.glolearn.newbook.repository;

import com.glolearn.newbook.domain.*;
import com.glolearn.newbook.dto.course.CourseSearchDto;
import com.glolearn.newbook.dto.course.Sort;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.glolearn.newbook.domain.QCourse.course;
import static com.glolearn.newbook.domain.QMember.member;

@Repository
@RequiredArgsConstructor
public class CourseRepository {

    private final EntityManager em;


    public void save(Course course) {
        em.persist(course);
    }

    // 인기강의 조회
    public List<Course> findAllByOrderByNumStudentDesc(Pageable pageable){
        int start = pageable.getPageNumber()* pageable.getPageSize();

        return em.createQuery("select c from Course c " +
                " left join fetch c.lecturer " +
                " where c.isPublished = true " +
                " order by c.numStudent ")
                .setFirstResult(start)
                .setMaxResults(pageable.getPageSize())
                .getResultList();
    }

    public Optional<Course> findById(Long courseId) {
        return Optional.ofNullable(em.find(Course.class, courseId));
    }

    public void delete(Course course) {
        em.remove(course);
    }

    public List<Course> findCourses(CourseSearchDto courseSearchDto){
        JPAQuery<Course> query = new JPAQuery<>(em);

        QCourse course = QCourse.course;
        QMember member = QMember.member;

        return  query.from(course).join(course.lecturer, member).fetchJoin()
                .where(
                        isPublished(),
                        eqCategory(courseSearchDto.getCategory()),
                        titleLike(courseSearchDto.getSearch())
                )
                .orderBy(getSort(courseSearchDto.getSort()))
                .offset((courseSearchDto.getPageNum()-1)* courseSearchDto.getPageSize())
                .limit(courseSearchDto.getPageSize())
                .fetch();
    }

    public List<Course> findCoursesByLecturer(Long lecturerId, CourseSearchDto courseSearchDto){
        JPAQuery<Course> query = new JPAQuery<>(em);

        QCourse course = QCourse.course;
        QMember member = QMember.member;

        return  query.from(course).join(course.lecturer, member).fetchJoin()
                .where(
                        eqCategory(courseSearchDto.getCategory()),
                        titleLike(courseSearchDto.getSearch()),
                        lecturedBy(lecturerId)
                )
                .orderBy(getSort(courseSearchDto.getSort()))
                .offset((courseSearchDto.getPageNum()-1)* courseSearchDto.getPageSize())
                .limit(courseSearchDto.getPageSize())
                .fetch();
    }

    public Long countCourses(CourseSearchDto courseSearchDto){
        JPAQuery<Course> query = new JPAQuery<>(em);

        QCourse course = QCourse.course;

        return  query.select(course.count()).from(course)
                .where(
                        isPublished(),
                        eqCategory(courseSearchDto.getCategory()),
                        titleLike(courseSearchDto.getSearch())
                )
                .orderBy(getSort(courseSearchDto.getSort()))
                .fetchOne();
    }

    public Long countCoursesByLecturer(Long lecturerId, CourseSearchDto courseSearchDto){
        JPAQuery<Course> query = new JPAQuery<>(em);

        QCourse course = QCourse.course;
        QMember member = QMember.member;

        return  query.select(course.count()).from(course)
                .join(course.lecturer, member)
                .where(
                        eqCategory(courseSearchDto.getCategory()),
                        titleLike(courseSearchDto.getSearch()),
                        lecturedBy(lecturerId)
                )
                .orderBy(getSort(courseSearchDto.getSort()))
                .fetchOne();
    }

    // 수강 중인 코스 조회
    public List<Course> findCoursesByMemberId(Long memberId, CourseSearchDto courseSearchDto){
        JPAQuery<Enrollment> query = new JPAQuery<>(em);

        QEnrollment enrollment = QEnrollment.enrollment;
        QCourse course = QCourse.course;
        QMember member = QMember.member;

        List<Enrollment> enrollments = query.from(enrollment)
                .join(enrollment.member, member).fetchJoin()
                .join(enrollment.course, course).fetchJoin()
                .where(
                        eqCategory(courseSearchDto.getCategory()),
                        titleLike(courseSearchDto.getSearch()),
                        enrolledBy(memberId)
                )
                .orderBy(getSort(courseSearchDto.getSort()))
                .offset((courseSearchDto.getPageNum() - 1) * courseSearchDto.getPageSize())
                .limit(courseSearchDto.getPageSize())
                .fetch();

        return enrollments.stream().map(e -> e.getCourse()).collect(Collectors.toList());
    }

    // 수강 중인 코스 조회 시 코스 수
    public Long countCoursesByMemberId(Long memberId, CourseSearchDto courseSearchDto){
        JPAQuery<Enrollment> query = new JPAQuery<>(em);

        QEnrollment enrollment = QEnrollment.enrollment;
        QCourse course = QCourse.course;
        QMember member = QMember.member;

        return query.select(course.count()).from(enrollment)
                .join(enrollment.member, member)
                .join(enrollment.course, course)
                .where(
                        eqCategory(courseSearchDto.getCategory()),
                        titleLike(courseSearchDto.getSearch()),
                        enrolledBy(memberId)
                )
                .orderBy(getSort(courseSearchDto.getSort()))
                .fetchOne();
    }

    private BooleanExpression enrolledBy(Long memberId) {
        return member.id.eq(memberId);
    }

    private BooleanExpression lecturedBy(Long lecturerId) {
        return member.id.eq(lecturerId);
    }

    private OrderSpecifier getSort(Sort sort) {
        if(sort == null){
            sort = Sort.RECENT;
        }

        if(sort.equals(Sort.RECENT)){
            return new OrderSpecifier(Order.DESC, course.regDate);
        }else if(sort.equals(Sort.POPULAR)){
            return new OrderSpecifier(Order.DESC, course.numStudent);
        }else{  //default
            return new OrderSpecifier(Order.DESC, course.regDate);
        }
    }

    private BooleanExpression isPublished() {
        return course.isPublished.eq(true);
    }

    private BooleanExpression titleLike(String search) {
        if(search == null){
            return null;
        }
        return course.title.like("%" + search + "%");
    }

    private BooleanExpression eqCategory(Category category){
        if(category == null){
            return  null;
        }
        return course.category.eq(category);
    }
}
