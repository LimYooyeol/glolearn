package com.glolearn.newbook.dto.lecture;

import com.glolearn.newbook.domain.Lecture;
import lombok.Data;

@Data
public class LectureDetailsDto {

    private Long lectureId;

    private Long courseId;

    private String courseTitle;

    private String lectureTitle;

    private String contents;


    public LectureDetailsDto(Lecture lecture) {
        this.lectureId = lecture.getId();
        this.courseId = lecture.getCourse().getId();
        this.courseTitle = lecture.getCourse().getTitle();
        this.lectureTitle = lecture.getTitle();
        this.contents = lecture.getContents().getContents();
    }
}
