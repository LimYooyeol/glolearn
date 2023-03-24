package com.glolearn.newbook.repository;

import com.glolearn.newbook.domain.Lecture;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LectureRepository {
    private final EntityManager em;

    public void save(Lecture lecture){
        em.persist(lecture);
    }

    public Lecture findById(Long lectureId){
        return em.find(Lecture.class, lectureId);
    }

    public List<Lecture> findAllByCourseId(Long courseId){
        return em.createQuery(
                "select l from Lecture l " +
                " where l.course.id =: courseId " +
                " order by l.id")
                .setParameter("courseId", courseId)
                .getResultList();
    }

    public Lecture findFirstLecture(Long courseId){
        List<Lecture> list =  em.createQuery(
                "select l from Lecture l " +
                        " where l.course.id =: courseId " +
                        " order by l.id ")
                .setParameter("courseId", courseId)
                .setMaxResults(1).getResultList();

        return (list.size() > 0) ? list.get(0) : null;
    }

    public void delete(Lecture lecture){
        em.remove(lecture);
    }

    public Lecture findNext(Long courseId, Long lectureId){
        List<Lecture> resultList = em.createQuery(
                        "select l from Lecture l " +
                                " where l.course.id =:courseId " +
                                " and l.id >:lectureId " +
                                " order by l.id"
                )
                .setParameter("courseId", courseId)
                .setParameter("lectureId", lectureId)
                .setMaxResults(1)
                .getResultList();

        if(resultList.size() == 0){
            return null;
        }

        return resultList.get(0);
    }

    public Lecture findPrev(Long courseId, Long lectureId){
        List<Lecture> resultList = em.createQuery(
                        "select l from Lecture l " +
                                " where l.course.id =:courseId " +
                                " and l.id <:lectureId " +
                                " order by l.id desc"
                )
                .setParameter("courseId", courseId)
                .setParameter("lectureId", lectureId)
                .setMaxResults(1)
                .getResultList();

        if(resultList.size() == 0){
            return null;
        }

        return resultList.get(0);
    }
}
