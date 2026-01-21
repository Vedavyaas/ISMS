package com.adl.isms.controller;

import com.adl.isms.dto.FacultyDTO;
import com.adl.isms.dto.StudentDTO;
import com.adl.isms.service.CSVService;
import org.jspecify.annotations.Nullable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@RestController
public class CSVController {

    private final CSVService csvService;

    public CSVController(CSVService csvService) {
        this.csvService = csvService;
    }

    @PostMapping("/students/csv")
    public String updateStudentsController(@RequestParam MultipartFile file) {
        if (file.isEmpty()) return "File is empty!";
        try {
            return csvService.updateStudentData(file);
        } catch (AccessDeniedException e){
            return "You dont have enough permission to perform this action";
        }
    }

    @PostMapping("/faculties/csv")
    public String updateFacultiesController(@RequestParam MultipartFile file) {
        if (file.isEmpty()) return "File is empty!";
        try {
            return csvService.updateFacultyData(file);
        } catch (AccessDeniedException e){
            return "You dont have enough permission to perform this action";
        }
    }

    @PostMapping("/student/csv")
    public String updateStudentController(@RequestBody StudentDTO studentDTO){
        try {
            return csvService.updateStudent(studentDTO);
        } catch (AccessDeniedException e){
            return "You dont have enough permission to perform this action";
        }
    }

    @PostMapping("/faculty/csv")
    public String updateFaculty(@RequestBody FacultyDTO facultyDTO){
        try {
            return csvService.updateFaculty(facultyDTO);
        } catch (AccessDeniedException e){
            return "You dont have enough permission to perform this action";
        }
    }
}
