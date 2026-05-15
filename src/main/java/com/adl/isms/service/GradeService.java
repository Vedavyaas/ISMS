package com.adl.isms.service;

import com.adl.isms.assests.PaymentStatus;
import com.adl.isms.dto.GradeDTO;
import com.adl.isms.dto.GradeViewDTO;
import com.adl.isms.repository.*;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final AttendanceRepository attendanceRepository;
    private final FinanceRepository financeRepository;
    private final EnrolmentRepository enrolmentRepository;

    public GradeService(GradeRepository gradeRepository,
                        StudentRepository studentRepository,
                        CourseRepository courseRepository,
                        AttendanceRepository attendanceRepository,
                        FinanceRepository financeRepository,
                        EnrolmentRepository enrolmentRepository) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.attendanceRepository = attendanceRepository;
        this.financeRepository = financeRepository;
        this.enrolmentRepository = enrolmentRepository;
    }

    // ─── Admin: update single grade ─────────────────────────────────────────

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @Transactional
    public String updateSingleGrade(GradeDTO dto) {
        StudentEntity student = studentRepository.findById(dto.studentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + dto.studentId()));
        CourseEntity course = courseRepository.findById(dto.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + dto.courseId()));

        if (dto.marksObtained() < 0 || dto.marksObtained() > 100)
            throw new IllegalArgumentException("Marks must be between 0 and 100");
        if (dto.gradePoint() < 0 || dto.gradePoint() > 10)
            throw new IllegalArgumentException("Grade point must be between 0 and 10");

        Optional<GradeEntity> existing =
                gradeRepository.findByStudent_IdAndCourse_Id(dto.studentId(), dto.courseId());

        GradeEntity entity = existing.orElseGet(() -> new GradeEntity(student, course, dto.marksObtained(), dto.gradePoint()));
        entity.setMarksObtained(dto.marksObtained());
        entity.setGradePoint(dto.gradePoint());
        gradeRepository.save(entity);

        return "Grade updated for student [" + student.getName() + "] in course [" + course.getCourseName() + "]";
    }

    // ─── Admin: bulk update via CSV ──────────────────────────────────────────

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @Transactional
    public String updateGradesFromCsv(MultipartFile file) {
        if (file.isEmpty()) return "File is empty!";

        List<GradeDTO> dtos;
        try {
            CsvMapper mapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            try (MappingIterator<GradeDTO> it =
                         mapper.readerFor(GradeDTO.class).with(schema).readValues(file.getInputStream())) {
                dtos = it.readAll();
            }
        } catch (IOException e) {
            return "Failed to parse CSV: " + e.getMessage();
        }

        if (dtos.isEmpty()) return "No records found in CSV";

        int updated = 0, failed = 0;
        StringBuilder errors = new StringBuilder();

        for (GradeDTO dto : dtos) {
            try {
                updateSingleGrade(dto);
                updated++;
            } catch (Exception e) {
                failed++;
                errors.append("Row [studentId=").append(dto.studentId())
                      .append(", courseId=").append(dto.courseId())
                      .append("]: ").append(e.getMessage()).append("; ");
            }
        }

        String result = "CSV processed — Updated: " + updated + ", Failed: " + failed;
        if (failed > 0) result += " | Errors: " + errors;
        return result;
    }

    // ─── Admin: view all grades ──────────────────────────────────────────────

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public List<GradeViewDTO> getAllGrades() {
        return gradeRepository.findAll().stream().map(this::toViewDTO).toList();
    }

    // ─── Student: view own grades (gated by finance + attendance) ───────────
    public List<StudentGradeResult> getMyGrades() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // 1. Finance gate: must be fully paid
        boolean financePaid = financeRepository
                .findByStudent_UserId_UserName(username)
                .map(f -> f.getPaymentStatus() == PaymentStatus.PAID)
                .orElse(false);

        List<GradeEntity> grades = gradeRepository.findAllByStudent_UserId_UserName(username);

        if (grades.isEmpty()) {
            return List.of();
        }

        // If finance not paid — return every course as locked with global reason
        if (!financePaid) {
            return grades.stream().map(g -> new StudentGradeResult(
                    false,
                    "Fees pending — please clear all outstanding dues to unlock your grades.",
                    "FINANCE_PENDING",
                    g.getCourse().getCourseName(),
                    g.getCourse().getCourseCode()
            )).toList();
        }

        // 2. Finance paid — check attendance per course
        return grades.stream().map(g -> {
            double attendance = attendanceRepository
                    .findByStudent_IdAndCourse_Id(g.getStudent().getId(), g.getCourse().getId())
                    .map(AttendanceEntity::getAttendancePercentage)
                    .orElse(0.0);

            if (attendance < 75.0) {
                return new StudentGradeResult(
                        false,
                        "Attendance is " + attendance + "% — minimum 75% required to view this grade.",
                        "LOW_ATTENDANCE",
                        g.getCourse().getCourseName(),
                        g.getCourse().getCourseCode()
                );
            }

            return new StudentGradeResult(toViewDTO(g));
        }).toList();
    }

    // ─── Faculty: view grades of students in their courses ──────────────────
    @PreAuthorize("hasAuthority('SCOPE_ROLE_FACULTY')")
    public List<GradeViewDTO> getFacultyGrades() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<EnrolmentEntity> enrolments = enrolmentRepository.findAllByFacultyEntity_User_UserName(username);
        
        List<GradeViewDTO> facultyGrades = new java.util.ArrayList<>();
        for (EnrolmentEntity enrolment : enrolments) {
            Optional<GradeEntity> grade = gradeRepository.findByStudent_IdAndCourse_Id(
                enrolment.getStudent().getId(), 
                enrolment.getCourse().getId()
            );
            grade.ifPresent(g -> facultyGrades.add(toViewDTO(g)));
        }
        return facultyGrades;
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private GradeViewDTO toViewDTO(GradeEntity e) {
        return new GradeViewDTO(
                e.getId(),
                e.getStudent().getId(),
                e.getStudent().getName(),
                e.getCourse().getId(),
                e.getCourse().getCourseName(),
                e.getCourse().getCourseCode(),
                e.getMarksObtained(),
                e.getGradePoint(),
                toLetterGrade(e.getMarksObtained())
        );
    }

    private String toLetterGrade(double marks) {
        if (marks >= 90) return "O";
        if (marks >= 80) return "A+";
        if (marks >= 70) return "A";
        if (marks >= 60) return "B+";
        if (marks >= 50) return "B";
        if (marks >= 40) return "C";
        return "F";
    }

    // ─── Inner result wrapper for student view ────────────────────────────────

    public record StudentGradeResult(
            boolean visible,
            String reason,        // null if visible; 'FINANCE_PENDING' or 'LOW_ATTENDANCE' if blocked
            String lockType,      // null if visible
            String courseName,
            String courseCode,
            GradeViewDTO grade    // populated only when visible=true
    ) {
        /** Visible grade */
        public StudentGradeResult(GradeViewDTO grade) {
            this(true, null, null, grade.courseName(), grade.courseCode(), grade);
        }

        /** Blocked grade (finance or attendance) */
        public StudentGradeResult(boolean visible, String reason, String lockType,
                                  String courseName, String courseCode) {
            this(visible, reason, lockType, courseName, courseCode, null);
        }
    }
}
