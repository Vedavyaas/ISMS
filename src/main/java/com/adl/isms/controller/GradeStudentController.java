package com.adl.isms.controller;

import com.adl.isms.service.GradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/student/grades")
public class GradeStudentController {

    private final GradeService gradeService;

    public GradeStudentController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @GetMapping
    public ResponseEntity<List<GradeService.StudentGradeResult>> getMyGrades() {
        return ResponseEntity.ok(gradeService.getMyGrades());
    }
}
