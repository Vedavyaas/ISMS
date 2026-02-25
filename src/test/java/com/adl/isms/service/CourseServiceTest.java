package com.adl.isms.service;

import com.adl.isms.dto.CourseDTO;
import com.adl.isms.repository.CourseEntity;
import com.adl.isms.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseService courseService;

    private CourseDTO courseDTO;
    private CourseEntity courseEntity;

    @BeforeEach
    void setUp() {
        courseDTO = new CourseDTO("Java Programming", "CS101", 4, 1);
        courseEntity = new CourseEntity("Java Programming", "CS101", 4, 1);
    }

    @Test
    public void createCourse_Success() {
        when(courseRepository.existsByCourseCode(anyString())).thenReturn(false);

        String result = courseService.createCourse(courseDTO);

        assertEquals("Course saved successfully", result);
        verify(courseRepository, times(1)).save(any(CourseEntity.class));
    }

    @Test
    public void createCourse_AlreadyExists() {
        when(courseRepository.existsByCourseCode(anyString())).thenReturn(true);

        String result = courseService.createCourse(courseDTO);

        assertEquals("Course already exists.", result);
        verify(courseRepository, never()).save(any());
    }


    @Test
    public void updateCourseName_Success() {
        when(courseRepository.findByCourseCode("CS101")).thenReturn(Optional.of(courseEntity));

        String result = courseService.updateCourseName("Python Programming", "CS101");

        assertEquals("Updated course", result);
        assertEquals("Python Programming", courseEntity.getCourseName());
        verify(courseRepository).save(courseEntity);
    }

    @Test
    public void updateCourseName_NotFound() {
        when(courseRepository.findByCourseCode(anyString())).thenReturn(Optional.empty());

        String result = courseService.updateCourseName("New Name", "INVALID");

        assertEquals("Course not found", result);
        verify(courseRepository, never()).save(any());
    }

    @Test
    public void deleteCourse_Success() {
        when(courseRepository.findByCourseCode("CS101")).thenReturn(Optional.of(courseEntity));

        String result = courseService.deleteCourse("CS101");

        assertEquals("Course deleted successfully", result);
        verify(courseRepository).delete(courseEntity);
    }

    @Test
    public void deleteCourse_NotFound() {
        when(courseRepository.findByCourseCode(anyString())).thenReturn(Optional.empty());

        String result = courseService.deleteCourse("INVALID");

        assertEquals("Course not found", result);
        verify(courseRepository, never()).delete(any());
    }

    @Test
    public void getAllCourses_Success() {
        Page<CourseDTO> mockPage = mock(Page.class);
        when(courseRepository.findAllProjectedBy(any(Pageable.class))).thenReturn(mockPage);
        when(mockPage.getContent()).thenReturn(List.of(courseDTO));
        when(mockPage.hasNext()).thenReturn(false);
        List<CourseDTO> result = courseService.getAllCourses();

        assertEquals(1, result.size());
        assertEquals("CS101", result.get(0).courseCode());
        verify(courseRepository).findAllProjectedBy(any(Pageable.class));
    }
}