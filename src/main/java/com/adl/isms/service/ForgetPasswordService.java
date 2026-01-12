package com.adl.isms.service;

import com.adl.isms.repository.UserEntity;
import com.adl.isms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ForgetPasswordService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public ForgetPasswordService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public String passwordReset(String username, String newPassword) {
        Optional<UserEntity> userEntity = userRepository.findByUserName(username);

        if (userEntity.isPresent()) {
            UserEntity user = userEntity.get();
            user.setPassword(passwordEncoder.encode(newPassword));

            userRepository.save(user);
            return "Password has been reset";
        }

        return "Invalid username";
    }
}
