package com.adl.isms.dto;

public record GradeViewDTO(
        Long gradeId,
        Long studentId,
        String studentName,
        Long courseId,
        String courseName,
        String courseCode,
        double marksObtained,
        double gradePoint,
        String letterGrade
) {
}
