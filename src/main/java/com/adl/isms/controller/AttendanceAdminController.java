package com.adl.isms.controller;

import com.adl.isms.dto.AttendanceDTO;
import com.adl.isms.dto.AttendanceViewDTO;
import com.adl.isms.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/admin/attendance")
@PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
public class AttendanceAdminController {

    private final AttendanceService attendanceService;

    public AttendanceAdminController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping
    public ResponseEntity<String> updateSingleAttendance(@RequestBody AttendanceDTO dto) {
        try {
            return ResponseEntity.ok(attendanceService.updateSingleAttendance(dto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/csv")
    public ResponseEntity<String> updateAttendanceCsv(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(attendanceService.updateAttendanceFromCsv(file));
    }

    @GetMapping
    public ResponseEntity<List<AttendanceViewDTO>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<AttendanceViewDTO>> getAttendanceByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(attendanceService.getAttendanceByCourse(courseId));
    }
}
