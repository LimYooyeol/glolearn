package com.glolearn.newbook.repository;

import com.glolearn.newbook.domain.Comment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.swing.text.html.parser.Entity;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepository {
    private final EntityManager em;

    // 댓글 추가
    public void save(Comment comment){
        em.persist(comment);
    }

    // 댓글 조회
    public Comment findById(Long id){
        return em.find(Comment.class, id);
    }

    // 댓글 삭제
    public void delete(Comment comment){
        em.remove(comment);
    }

    // 댓글 목록 조회
    public List<Comment> findAllByLectureId(Long lectureId){
        String query =
                "select c from Comment c " +
                " where c.lecture.id =:lectureId " +
                " order by c.root.id ASC, c.id ASC ";

        List<Comment> comments = em.createQuery(query)
                .setParameter("lectureId", lectureId)
                .getResultList();

        return comments;
    }

}

