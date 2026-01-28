package com.adl.isms.service;

import com.adl.isms.dto.CourseDTO;
import com.adl.isms.repository.CourseEntity;
import com.adl.isms.repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public String createCourse(CourseDTO courseDTO) {
        CourseEntity courseEntity = new CourseEntity(courseDTO.courseName(), courseDTO.courseCode(), courseDTO.credits());
        if(courseRepository.existsByCourseCode(courseDTO.courseCode())) return "Course already exists.";

        courseRepository.save(courseEntity);
        return "Course saved successfully";
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    @Transactional
    public String updateCourseName(String courseName, String courseCode) {
        Optional<CourseEntity> course = courseRepository.findByCourseCode(courseCode);

        if (course.isEmpty()) return "Course not found";
        course.get().setCourseName(courseName);
        courseRepository.save(course.get());

        return "Updated course";
    }

    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public String deleteCourse(String courseCode) {
        Optional<CourseEntity> course = courseRepository.findByCourseCode(courseCode);

        if (course.isEmpty()) return "Course not found";

        courseRepository.delete(course.get());
        return "Course deleted successfully";
    }

    public List<CourseDTO> getAllCourses() {
        List<CourseDTO> courseDTOS = new ArrayList<>();

        int page = 0;
        Page<CourseDTO> courseDTOPage;

        do {
            Pageable pageable = PageRequest.of(page++, 10);
            courseDTOPage = courseRepository.findAllProjectedBy(pageable);

            courseDTOS.addAll(courseDTOPage.getContent());

        } while (courseDTOPage.hasNext());

        return courseDTOS;
    }
}
