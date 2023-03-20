package com.glolearn.newbook.dto.lecture;

import com.glolearn.newbook.domain.Lecture;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LecturePreviewDto {
    private Long courseId;

    private Long lectureId;

    private String title;

    private String courseTitle;

    private LocalDate regDate;

    private LocalDate lastUpdateDate;

    public LecturePreviewDto(Lecture l) {
        this.courseId = l.getCourse().getId();
        this.lectureId = l.getId();
        this.title = l.getTitle();
        this.regDate = l.getRegDate().toLocalDate();
        this.lastUpdateDate = l.getLastUpdateDate().toLocalDate();
        this.courseTitle = l.getCourse().getTitle();
    }
}
