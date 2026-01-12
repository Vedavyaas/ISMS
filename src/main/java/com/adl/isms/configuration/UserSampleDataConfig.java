package com.adl.isms.configuration;

import com.adl.isms.assests.Role;
import com.adl.isms.repository.UserEntity;
import com.adl.isms.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class UserSampleDataConfig {

    @Bean
    ApplicationRunner userSampleData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0) {
                return;
            }
            userRepository.save(new UserEntity("admin", passwordEncoder.encode("admin123"), Role.ADMIN));
            userRepository.save(new UserEntity("faculty", passwordEncoder.encode("faculty123"), Role.FACULTY));
            userRepository.save(new UserEntity("student", passwordEncoder.encode("student123"), Role.STUDENT));
        };
    }
}