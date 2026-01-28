package com.adl.isms.controller;

import com.adl.isms.dto.CourseDTO;
import com.adl.isms.service.CourseService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/post/course")
    public String createCourse(@RequestBody CourseDTO courseDTO) {
        try{
            return courseService.createCourse(courseDTO);
        } catch (AccessDeniedException e) {
            return "You dont have enough permissions to perform this action";
        }
    }

    @PutMapping("/update/course")
    public String updateCourse(@RequestParam String courseName, @RequestParam String courseCode) {
        try{
            return courseService.updateCourseName(courseName, courseCode);
        } catch (AccessDeniedException e) {
            return "You dont have enough permissions to perform this action";
        }
    }

    @DeleteMapping("/delete/course")
    public String deleteCourse(@RequestParam String courseCode){
        try{
            return courseService.deleteCourse(courseCode);
        } catch (AccessDeniedException e) {
            return "You dont have enough permissions to perform this action";
        }
    }

    @GetMapping("/get/courses")
    public List<CourseDTO> courseEntityList() {
        return courseService.getAllCourses();
    }
}
