package com.adl.isms.controller;

import com.adl.isms.dto.FacultyCourseViewDTO;
import com.adl.isms.dto.StudentCourseViewDTO;
import com.adl.isms.service.EnrollmentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/enrolment")
public class EnrolmentController {

    private final EnrollmentService enrollmentService;

    public EnrolmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @GetMapping("/student")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_STUDENT')")
    public List<StudentCourseViewDTO> getStudentCourses(@AuthenticationPrincipal Jwt jwt) {
        return enrollmentService.getStudentCourses(jwt.getSubject());
    }

    @GetMapping("/faculty")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_FACULTY')")
    public List<FacultyCourseViewDTO> getFacultyCourses(@AuthenticationPrincipal Jwt jwt) {
        return enrollmentService.getFacultyCourses(jwt.getSubject());
    }
}
