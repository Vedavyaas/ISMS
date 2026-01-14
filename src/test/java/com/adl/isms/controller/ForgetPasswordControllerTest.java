package com.adl.isms.controller;

import com.adl.isms.service.ForgetPasswordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ForgetPasswordController.class)
public class ForgetPasswordControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ForgetPasswordService forgetPasswordService;

    @Test
    @WithMockUser("Username")
    public void passwordResetTest1() throws Exception {
        when(forgetPasswordService.passwordReset(anyString(), anyString())).thenReturn("Password reset successful");

        mockMvc.perform(post("/password/reset")
                .with(csrf())
                .param("username", "Username")
                .param("newPassword", "password"))
                .andExpect(status().isOk())
                .andExpect(content().string("Password reset successful"));
    }
}
