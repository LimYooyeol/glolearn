package com.glolearn.newbook.dto.course;

import com.glolearn.newbook.domain.Course;
import lombok.Data;

@Data
public class CoursePreviewDto {
    private Long courseId;

    private String cover;

    private String title;

    private Long numStudent;

    private String lecturer;

    private Boolean isPublished;

    public CoursePreviewDto(){}
    public CoursePreviewDto(Course c) {
        this.courseId = c.getId();
        this.cover = c.getCover();
        this.title = c.getTitle();
        this.numStudent = c.getNumStudent();
        this.lecturer = c.getLecturer().getNickname();
        this.isPublished = c.getIsPublished();
    }
}
