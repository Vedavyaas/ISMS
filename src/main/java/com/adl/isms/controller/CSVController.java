package com.adl.isms.controller;

import com.adl.isms.service.CSVService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class CSVController {

    private final CSVService csvService;

    public CSVController(CSVService csvService) {
        this.csvService = csvService;
    }

    @PostMapping("/students/csv")
    public String updateStudentController(@RequestParam MultipartFile file) {
        if (file.isEmpty()) {
            return "File is empty!";
        }
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return csvService.updateStudentData(user, file);
    }

    @PostMapping("/faculties/csv")
    public String updateFacultyController(@RequestParam MultipartFile file) {
        if (file.isEmpty()) return "File is empty!";
        String user = SecurityContextHolder.getContext().getAuthentication().getName();
        return csvService.updateFacultyData(user, file);
    }
}
