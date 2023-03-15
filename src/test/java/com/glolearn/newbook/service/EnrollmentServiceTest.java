package com.glolearn.newbook.service;

import com.glolearn.newbook.annotation.Auth;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EnrollmentServiceTest {
    @Autowired EnrollmentService enrollmentService;

    @Test
    public void 존재하지_않는_경우_테스트(){
        //given

        //when, then
        assertNull(enrollmentService.findByMemberIdAndCourseId(-1L, -1L));
    }
}