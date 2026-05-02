package com.adl.isms.controller;

import com.adl.isms.dto.GradeDTO;
import com.adl.isms.dto.GradeViewDTO;
import com.adl.isms.service.GradeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/grades")
@PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
public class GradeAdminController {

    private final GradeService gradeService;

    public GradeAdminController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    /** Update / create a single student grade */
    @PostMapping
    public ResponseEntity<String> updateSingleGrade(@RequestBody GradeDTO dto) {
        try {
            return ResponseEntity.ok(gradeService.updateSingleGrade(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /** Bulk update grades via CSV upload (columns: studentId, courseId, marksObtained, gradePoint) */
    @PostMapping("/csv")
    public ResponseEntity<String> updateGradesFromCsv(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(gradeService.updateGradesFromCsv(file));
    }

    /** Get all grade records */
    @GetMapping
    public ResponseEntity<List<GradeViewDTO>> getAllGrades() {
        return ResponseEntity.ok(gradeService.getAllGrades());
    }
}
