package com.adl.isms.service;

import com.adl.isms.assests.Role;
import com.adl.isms.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @Test
    public void updateFacultyDataTest1() {
        when(userRepository.findByUserName(null)).thenReturn(Optional.empty());

        assertEquals("User not found", csvService.updateStudentData(null, new MockMultipartFile("file", "test.txt", "application/octet-stream", "test".getBytes())));
    }

    @Test
    public void updateStudentDataTest2() {
        when(userRepository.findByUserName("Username")).thenReturn(Optional.of(new UserEntity("Username", "password", Role.STUDENT)));

        assertEquals("You don't have enough permissions!", csvService.updateStudentData("Username", new MockMultipartFile("file", "test.txt", "application/octet-stream", "test".getBytes())));
        verify(userRepository).findByUserName("Username");
    }

    @Test
    public void updateStudentDataTest3() {
        when(userRepository.findByUserName("Username")).thenReturn(Optional.of(new UserEntity("Username", "password", Role.FACULTY)));

        assertEquals("You don't have enough permissions!", csvService.updateStudentData("Username", new MockMultipartFile("file", "test.txt", "application/octet-stream", "test".getBytes())));
        verify(userRepository).findByUserName("Username");
    }

    @Test
    public void updateStudentDataTest4() {
        String csvContent = "John Doe,2000-01-01,john@test.com,ACTIVELY_STUDYING";
        MockMultipartFile file = new MockMultipartFile(
                "file", "students.csv", "text/csv", csvContent.getBytes()
        );
        when(userRepository.findByUserName("Username"))
                .thenReturn(Optional.of(new UserEntity("Username", "password", Role.ADMIN)));


        assertEquals("Successfully updated Student data!", csvService.updateStudentData("Username", file));

        verify(userRepository).findByUserName(anyString());
        verify(studentRepository).saveAll(anyList());
    }

    @Test
    public void updateStudentDataTest5() {
        when(userRepository.findByUserName("Username")).thenReturn(Optional.of(new UserEntity("Username", "password", Role.ADMIN)));

        assertEquals("Failed to update data", csvService.updateStudentData("Username", null));

        verify(userRepository).findByUserName(anyString());
    }

    @Test
    public void updateStudentDataTest1() {
        when(userRepository.findByUserName(null)).thenReturn(Optional.empty());

        assertEquals("User not found", csvService.updateStudentData(null, new MockMultipartFile("file", "test.txt", "application/octet-stream", "test".getBytes())));
    }

    @Test
    public void updateFacultyDataTest2() {
        when(userRepository.findByUserName("Username")).thenReturn(Optional.of(new UserEntity("Username", "password", Role.STUDENT)));

        assertEquals("You don't have enough permissions!", csvService.updateStudentData("Username", new MockMultipartFile("file", "test.txt", "application/octet-stream", "test".getBytes())));
        verify(userRepository).findByUserName(anyString());
    }

    @Test
    public void updateFacultyDataTest3() {
        when(userRepository.findByUserName("Username")).thenReturn(Optional.of(new UserEntity("Username", "password", Role.FACULTY)));

        assertEquals("You don't have enough permissions!", csvService.updateStudentData("Username", new MockMultipartFile("file", "test.txt", "application/octet-stream", "test".getBytes())));
        verify(userRepository).findByUserName(anyString());
    }

    @Test
    public void updateFacultyDataTest4() {
        String csvContent = "name,email,department,position,Dr. Smith,smith@university.edu,Computer Science,PROFESSOR";
        MockMultipartFile file = new MockMultipartFile(
                "file", "students.csv", "text/csv", csvContent.getBytes()
        );

        when(userRepository.findByUserName("Username"))
                .thenReturn(Optional.of(new UserEntity("Username", "password", Role.ADMIN)));


        assertEquals("Successfully updated Faculty data!", csvService.updateFacultyData("Username", file));

        verify(userRepository).findByUserName(anyString());
        verify(facultyRepository).saveAll(anyList());
    }

    @Test
    public void updateFacultyDataTest5() {
        when(userRepository.findByUserName("Username")).thenReturn(Optional.of(new UserEntity("Username", "password", Role.ADMIN)));

        assertEquals("Failed to update data", csvService.updateFacultyData("Username", null));
        verify(userRepository).findByUserName(anyString());
    }
}