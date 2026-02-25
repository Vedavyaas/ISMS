package com.adl.isms.service;

import com.adl.isms.assests.Department;
import com.adl.isms.assests.Designation;
import com.adl.isms.assests.EnrolmentStatus;
import com.adl.isms.assests.Role;
import com.adl.isms.dto.FacultyDTO;
import com.adl.isms.dto.StudentDTO;
import com.adl.isms.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CSVServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private FacultyRepository facultyRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CSVService csvService;

    private FacultyDTO facultyDTO;
    private StudentDTO studentDTO;

    @BeforeEach
    void setUp() {
        facultyDTO = new FacultyDTO("John Doe", "john.doe@example.com", Department.CSE, Designation.PROFESSOR);
        studentDTO = new StudentDTO("Jane Doe", LocalDate.of(2000, 1, 1), "jane.doe@example.com", EnrolmentStatus.COMPLETED_STUDY, 3, Department.CSE);
    }

    @Test
    public void updateFaculty_Success() {
        when(facultyRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        String result = csvService.updateFaculty(facultyDTO);

        assertEquals("Successfully updated faculty data", result);
        verify(facultyRepository, times(1)).save(any(FacultyEntity.class));
    }

    @Test
    public void updateFaculty_AlreadyExists() {
        when(facultyRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        String result = csvService.updateFaculty(facultyDTO);

        assertEquals("Already exists", result);
        verify(facultyRepository, never()).save(any());
    }

    @Test
    public void updateStudent_Success() {
        when(studentRepository.existsByEmail("jane.doe@example.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        String result = csvService.updateStudent(studentDTO);

        assertEquals("Successfully updated student data", result);
        verify(studentRepository, times(1)).save(any(StudentEntity.class));
    }

    @Test
    public void updateStudent_AlreadyExists() {
        when(studentRepository.existsByEmail("jane.doe@example.com")).thenReturn(true);

        String result = csvService.updateStudent(studentDTO);

        assertEquals("Already exists", result);
        verify(studentRepository, never()).save(any());
    }

    @Test
    public void updateStudentData_Success() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "students.csv", "text/csv", "name,dateOfBirth,email,enrolmentStatus,currentSemester,department\nJane Doe,2000-01-01,jane.doe@example.com,Enrolled,3,CSE".getBytes());
        
        CSVService spyCsvService = Mockito.spy(csvService);
        List<StudentDTO> dtos = new ArrayList<>();
        dtos.add(studentDTO);
        doReturn(dtos).when(spyCsvService).parseStudentCsv(any());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        String result = spyCsvService.updateStudentData(file);

        assertEquals("Successfully updated Student data!", result);
        verify(studentRepository, times(1)).saveAllAndFlush(anyList());
    }

    @Test
    public void updateStudentData_DuplicateData() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "students.csv", "text/csv", "".getBytes());
        
        CSVService spyCsvService = Mockito.spy(csvService);
        List<StudentDTO> dtos = new ArrayList<>();
        dtos.add(studentDTO);
        dtos.add(studentDTO); // Duplicate email
        doReturn(dtos).when(spyCsvService).parseStudentCsv(any());

        String result = spyCsvService.updateStudentData(file);

        assertEquals("Duplicate data found", result);
        verify(studentRepository, never()).saveAllAndFlush(anyList());
    }
    
    @Test
    public void updateFacultyData_Success() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "faculties.csv", "text/csv", "name,email,department,designation\nJohn Doe,john.doe@example.com,CSE,Professor".getBytes());
        
        CSVService spyCsvService = Mockito.spy(csvService);
        List<FacultyDTO> dtos = new ArrayList<>();
        dtos.add(facultyDTO);
        doReturn(dtos).when(spyCsvService).parseFacultyCsv(any());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        String result = spyCsvService.updateFacultyData(file);

        assertEquals("Successfully updated Faculty data!", result);
        verify(facultyRepository, times(1)).saveAllAndFlush(anyList());
    }

    @Test
    public void updateFacultyData_DuplicateData() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "faculties.csv", "text/csv", "".getBytes());
        
        CSVService spyCsvService = Mockito.spy(csvService);
        List<FacultyDTO> dtos = new ArrayList<>();
        dtos.add(facultyDTO);
        dtos.add(facultyDTO);
        doReturn(dtos).when(spyCsvService).parseFacultyCsv(any());

        String result = spyCsvService.updateFacultyData(file);

        assertEquals("Duplicate data found", result);
        verify(facultyRepository, never()).saveAllAndFlush(anyList());
    }
}
