package com.adl.isms.service;

import com.adl.isms.dto.AttendanceDTO;
import com.adl.isms.dto.AttendanceViewDTO;
import com.adl.isms.repository.*;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.databind.MappingIterator;
import jakarta.transaction.Transactional;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             StudentRepository studentRepository,
                             CourseRepository courseRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @Transactional
    public String updateSingleAttendance(AttendanceDTO dto) {
        StudentEntity student = studentRepository.findById(dto.studentId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + dto.studentId()));
        CourseEntity course = courseRepository.findById(dto.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + dto.courseId()));

        if (dto.attendance() < 0 || dto.attendance() > 100) {
            throw new IllegalArgumentException("Attendance percentage must be between 0 and 100");
        }

        Optional<AttendanceEntity> existing =
                attendanceRepository.findByStudent_IdAndCourse_Id(dto.studentId(), dto.courseId());

        AttendanceEntity entity = existing.orElseGet(() ->
                new AttendanceEntity(student, course, dto.attendance()));
        entity.setAttendancePercentage(dto.attendance());
        attendanceRepository.save(entity);

        return "Attendance updated for student [" + student.getName() + "] in course [" + course.getCourseName() + "]";
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @Transactional
    public String updateAttendanceFromCsv(MultipartFile file) {
        if (file.isEmpty()) return "File is empty!";

        List<AttendanceDTO> dtos;
        try {
            dtos = parseCsv(file);
        } catch (IOException e) {
            return "Failed to parse CSV: " + e.getMessage();
        }

        if (dtos.isEmpty()) return "No records found in CSV";

        int updated = 0;
        int failed = 0;
        StringBuilder errors = new StringBuilder();

        for (AttendanceDTO dto : dtos) {
            try {
                updateSingleAttendance(dto);
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

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public List<AttendanceViewDTO> getAllAttendance() {
        return attendanceRepository.findAll().stream()
                .map(this::toViewDTO)
                .toList();
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public List<AttendanceViewDTO> getAttendanceByCourse(Long courseId) {
        return attendanceRepository.findAllByCourse_Id(courseId).stream()
                .map(this::toViewDTO)
                .toList();
    }

    public List<AttendanceViewDTO> getMyAttendance() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return attendanceRepository.findAllByStudent_UserId_UserName(username).stream()
                .map(this::toViewDTO)
                .toList();
    }

    private List<AttendanceDTO> parseCsv(MultipartFile file) throws IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader();
        try (MappingIterator<AttendanceDTO> it =
                     mapper.readerFor(AttendanceDTO.class).with(schema).readValues(file.getInputStream())) {
            return it.readAll();
        }
    }

    private AttendanceViewDTO toViewDTO(AttendanceEntity e) {
        return new AttendanceViewDTO(
                e.getId(),
                e.getStudent().getId(),
                e.getStudent().getName(),
                e.getCourse().getId(),
                e.getCourse().getCourseName(),
                e.getCourse().getCourseCode(),
                e.getAttendancePercentage()
        );
    }
}
