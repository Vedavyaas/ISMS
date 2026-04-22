package com.adl.isms.controller;

import com.adl.isms.dto.FacultyDTO;
import com.adl.isms.dto.StudentDTO;
import com.adl.isms.repository.FacultyRepository;
import com.adl.isms.repository.StudentRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
public class AdminUsersController {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;

    public AdminUsersController(StudentRepository studentRepository, FacultyRepository facultyRepository) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
    }

    @GetMapping("/students")
    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(s -> new StudentDTO(
                        s.getName(),
                        s.getDateOfBirth(),
                        s.getEmail(),
                        s.getEnrolmentStatus(),
                        s.getCurrentSemester(),
                        s.getDepartment()))
                .collect(Collectors.toList());
    }

    @GetMapping("/faculties")
    public List<FacultyDTO> getAllFaculties() {
        return facultyRepository.findAll().stream()
                .map(f -> new FacultyDTO(
                        f.getName(),
                        f.getEmail(),
                        f.getDepartment(),
                        f.getDesignation()))
                .collect(Collectors.toList());
    }
}
