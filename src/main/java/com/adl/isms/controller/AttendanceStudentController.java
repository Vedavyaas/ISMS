package com.adl.isms.controller;

import com.adl.isms.dto.AttendanceViewDTO;
import com.adl.isms.service.AttendanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/student/attendance")
public class AttendanceStudentController {

    private final AttendanceService attendanceService;

    public AttendanceStudentController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public ResponseEntity<List<AttendanceViewDTO>> getMyAttendance() {
        return ResponseEntity.ok(attendanceService.getMyAttendance());
    }
}
