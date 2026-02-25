package com.adl.isms.service;

import com.adl.isms.assests.Role;
import com.adl.isms.repository.UserEntity;
import com.adl.isms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ForgetPasswordServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ForgetPasswordService forgetPasswordService;

    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity("testuser", "oldpassword", Role.STUDENT);
    }

    @Test
    public void passwordReset_Success() {
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");

        String result = forgetPasswordService.passwordReset("testuser", "newpassword");

        assertEquals("Password has been reset", result);
        assertEquals("encodedNewPassword", userEntity.getPassword());
        verify(userRepository).save(userEntity);
    }

    @Test
    public void passwordReset_InvalidPassword_Null() {
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(userEntity));

        String result = forgetPasswordService.passwordReset("testuser", null);

        assertEquals("Invalid Password", result);
        verify(userRepository, never()).save(any());
    }

    @Test
    public void passwordReset_InvalidPassword_Empty() {
        when(userRepository.findByUserName("testuser")).thenReturn(Optional.of(userEntity));

        String result = forgetPasswordService.passwordReset("testuser", "");

        assertEquals("Invalid Password", result);
        verify(userRepository, never()).save(any());
    }

    @Test
    public void passwordReset_InvalidUsername() {
        when(userRepository.findByUserName(anyString())).thenReturn(Optional.empty());

        String result = forgetPasswordService.passwordReset("invaliduser", "newpassword");

        assertEquals("Invalid username", result);
        verify(userRepository, never()).save(any());
    }
}
