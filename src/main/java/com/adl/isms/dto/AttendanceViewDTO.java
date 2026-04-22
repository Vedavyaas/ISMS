package com.adl.isms.dto;

public record AttendanceViewDTO(
        Long attendanceId,
        Long studentId,
        String studentName,
        Long courseId,
        String courseName,
        String courseCode,
        double attendancePercentage
) {
}
