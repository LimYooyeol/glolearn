package com.glolearn.newbook.repository;

import com.glolearn.newbook.domain.LastLectureHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LastLectureHistoryRepository {
    private final EntityManager em;

    // 시청 기록 조회(member & course)
    public LastLectureHistory findByMemberIdAndCourseId(Long memberId, Long courseId){
        String query =
                "select h from LastLectureHistory h " +
                " join fetch h.lecture " +
                " where h.member.id =: memberId " +
                " and" +
                " h.course.id =: courseId";

        List<LastLectureHistory> resultList = em.createQuery(query)
                .setParameter("memberId", memberId)
                .setParameter("courseId", courseId)
                .getResultList();

        if(resultList.size() == 0){
            return null;
        }

        return resultList.get(0);
    }

    // 시청 기록 저장
    public void save(LastLectureHistory lastLectureHistory){
        em.persist(lastLectureHistory);
    }

}
