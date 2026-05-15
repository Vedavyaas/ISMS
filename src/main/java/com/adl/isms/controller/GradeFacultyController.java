package com.adl.isms.controller;

import com.adl.isms.dto.GradeViewDTO;
import com.adl.isms.service.GradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/faculty/grades")
@PreAuthorize("hasAuthority('SCOPE_ROLE_FACULTY')")
public class GradeFacultyController {

    private final GradeService gradeService;

    public GradeFacultyController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @GetMapping
    public ResponseEntity<List<GradeViewDTO>> getFacultyGrades() {
        return ResponseEntity.ok(gradeService.getFacultyGrades());
    }
}
