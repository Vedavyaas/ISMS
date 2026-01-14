package com.adl.isms.controller;

import com.adl.isms.service.CSVService;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CSVController.class)
public class CSVControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CSVService csvService;

    @Test
    @WithMockUser("Username")
    public void testStudentControllerTest1() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "students.csv",
                "text/csv",
                "name,dob,email,status\nJohn,2000-01-01,j@test.com,ACTIVE".getBytes()
        );

        when(csvService.updateStudentData(eq("Username"), any())).thenReturn("Data updated Successfully");

        mockMvc.perform(multipart("/students/csv")
                        .file(mockFile)
                        .with(csrf())
                        .param("username", "Username"))
                .andExpect(status().isOk())
                .andExpect(content().string("Data updated Successfully"));
    }

    @Test
    @WithMockUser("Username")
    public void testFacultyControllerTest1() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "faculties.csv",
                "text/csv",
                "name,department,designation\nJohn,CSE,PROFESSOR".getBytes()
        );

        when(csvService.updateFacultyData(any(), any())).thenReturn("Data updated Successfully");

        mockMvc.perform(multipart("/faculties/csv")
                .file(mockFile)
                .with(csrf())
                .param("username", "Username"))
                .andExpect(status().isOk())
                .andExpect(content().string("Data updated Successfully"));
    }
}