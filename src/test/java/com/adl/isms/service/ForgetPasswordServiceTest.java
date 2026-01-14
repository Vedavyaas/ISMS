package com.adl.isms.service;

import com.adl.isms.assests.Role;
import com.adl.isms.repository.UserEntity;
import com.adl.isms.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ForgetPasswordServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private ForgetPasswordService forgetPasswordService;

    @Test
    public void passwordResetTest1(){
        when(userRepository.findByUserName(null)).thenReturn(Optional.empty());

        assertEquals("Invalid username", forgetPasswordService.passwordReset(null, null));
    }

    @Test
    public void passwordResetTest2(){
        when(userRepository.findByUserName("Username")).thenReturn(Optional.of(new UserEntity("Username", "password", Role.STUDENT)));

        assertEquals("Invalid Password", forgetPasswordService.passwordReset("Username", null));
    }

    @Test
    public void passwordResetTest3(){
        when(userRepository.findByUserName("Username")).thenReturn(Optional.of(new UserEntity("Username", "password", Role.STUDENT)));

        when(passwordEncoder.encode("password")).thenReturn("hashed_password");
        assertEquals("Password has been reset",  forgetPasswordService.passwordReset("Username", "password"));

        verify(userRepository).save(any());
    }
}
