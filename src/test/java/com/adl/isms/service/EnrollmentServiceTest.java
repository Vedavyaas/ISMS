package com.adl.isms.service;

import com.adl.isms.assests.Department;
import com.adl.isms.assests.Designation;
import com.adl.isms.assests.EnrolmentStatus;
import com.adl.isms.assests.Role;
import com.adl.isms.dto.FacultyCourseViewDTO;
import com.adl.isms.dto.StudentCourseViewDTO;
import com.adl.isms.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EnrollmentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private EnrolmentRepository enrolmentRepository;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private StudentEntity studentEntity;
    private FacultyEntity facultyEntity;
    private CourseEntity courseEntity;
    private EnrolmentEntity enrolmentEntity;

    @BeforeEach
    void setUp() {
        UserEntity studentUser = new UserEntity("student1", "pass", Role.STUDENT);
        studentEntity = new StudentEntity(studentUser, "Student One", LocalDate.now(), "student1@example.com", EnrolmentStatus.COMPLETED_STUDY, 3, Department.CSE);

        UserEntity facultyUser = new UserEntity("faculty1", "pass", Role.FACULTY);
        facultyEntity = new FacultyEntity(facultyUser, "Faculty One", "faculty1@example.com", Department.CSE, Designation.PROFESSOR);

        courseEntity = new CourseEntity("Java Programming", "CS101", 4, 3);
        
        enrolmentEntity = new EnrolmentEntity();
        enrolmentEntity.setStudent(studentEntity);
        enrolmentEntity.setCourse(courseEntity);
        enrolmentEntity.setFacultyEntity(facultyEntity);
        enrolmentEntity.setSemesterID("SEM_3_2026");
    }

    @Test
    public void scheduleAllStudents_Success() {
        List<StudentEntity> students = new ArrayList<>();
        students.add(studentEntity);
        
        List<FacultyEntity> faculties = new ArrayList<>();
        faculties.add(facultyEntity);
        
        List<CourseEntity> courses = new ArrayList<>();
        courses.add(courseEntity);

        when(studentRepository.findAll()).thenReturn(students);
        when(facultyRepository.findAll()).thenReturn(faculties);
        when(courseRepository.findAllBySemester(3)).thenReturn(courses);

        enrollmentService.scheduleAllStudents();

        verify(enrolmentRepository, times(1)).save(any(EnrolmentEntity.class));
    }

    @Test
    public void scheduleAllStudents_NoFaculty() {
        List<StudentEntity> students = new ArrayList<>();
        students.add(studentEntity);

        when(studentRepository.findAll()).thenReturn(students);
        when(facultyRepository.findAll()).thenReturn(new ArrayList<>());

        enrollmentService.scheduleAllStudents();

        verify(enrolmentRepository, never()).save(any(EnrolmentEntity.class));
        verify(courseRepository, never()).findAllBySemester(anyInt());
    }

    @Test
    public void getStudentCourses_Success() {
        List<EnrolmentEntity> enrolments = new ArrayList<>();
        enrolments.add(enrolmentEntity);
        
        when(enrolmentRepository.findAllByStudent_UserId_UserName("student1")).thenReturn(enrolments);

        List<StudentCourseViewDTO> result = enrollmentService.getStudentCourses("student1");

        assertEquals(1, result.size());
        assertEquals("Java Programming", result.get(0).courseName());
        assertEquals("CS101", result.get(0).courseCode());
        assertEquals("Faculty One", result.get(0).facultyName());
        assertEquals("SEM_3_2026", result.get(0).semester());
    }

    @Test
    public void getFacultyCourses_Success() {
        List<EnrolmentEntity> enrolments = new ArrayList<>();
        enrolments.add(enrolmentEntity);
        
        when(enrolmentRepository.findAllByFacultyEntity_User_UserName("faculty1")).thenReturn(enrolments);

        List<FacultyCourseViewDTO> result = enrollmentService.getFacultyCourses("faculty1");

        assertEquals(1, result.size());
        assertEquals("Java Programming", result.get(0).courseName());
        assertEquals("CS101", result.get(0).courseCode());
        assertEquals("SEM_3_2026", result.get(0).semester());
    }
}
