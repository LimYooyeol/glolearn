package com.glolearn.newbook.service;

import com.glolearn.newbook.domain.Course;
import com.glolearn.newbook.domain.LastLectureHistory;
import com.glolearn.newbook.domain.Lecture;
import com.glolearn.newbook.domain.Member;
import com.glolearn.newbook.repository.CourseRepository;
import com.glolearn.newbook.repository.LastLectureHistoryRepository;
import com.glolearn.newbook.repository.LectureRepository;
import com.glolearn.newbook.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LastLectureHistoryService {
    private final LastLectureHistoryRepository lastLectureHistoryRepository;
    private final MemberRepository memberRepository;
    private final CourseRepository courseRepository;
    private final LectureRepository lectureRepository;

    // 시청 강의 번호 조회
    public Long findByMemberIdAndCourseId(Long memberId, Long courseId){
        Member member = memberRepository.findById(memberId);
        Course course = courseRepository.findById(courseId).orElse(null);

        if(member == null || course == null) {
            throw new IllegalArgumentException("회원이나 코스가 존재하지 않습니다.");
        }

        LastLectureHistory history = lastLectureHistoryRepository.findByMemberIdAndCourseId(memberId, courseId);
        if(history == null) {
            return null;
        }

        return history.getLecture().getId();
    }

    // 시청 기록 추가 or 업데이트
    @Transactional
    public Long logHistory(Long memberId, Long courseId, Long lectureId){
        Member member = memberRepository.findById(memberId);
        Course course = courseRepository.findById(courseId).orElse(null);
        Lecture lecture = lectureRepository.findById(lectureId);
        if(member == null || course == null || lecture == null){
            throw new IllegalArgumentException("잘못된 시청기록 입니다.");
        }

        LastLectureHistory history = lastLectureHistoryRepository.findByMemberIdAndCourseId(memberId, courseId);

        // 새로운 시청기록인 경우 추가
        if(history == null){
            history = LastLectureHistory.createLastLectureHistory(member, course, lecture);

            lastLectureHistoryRepository.save(history);
        } // 이미 있는 기록인 경우 강의만 업데이트
        else {
            history.update(lecture);
        }

        return history.getId();
    }

}
