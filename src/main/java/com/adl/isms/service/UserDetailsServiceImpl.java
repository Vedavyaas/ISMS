package com.adl.isms.service;

import com.adl.isms.repository.UserEntity;
import com.adl.isms.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found :" + username));

        return User.withUsername(user.getUserName())
                .password(user.getPassword())
                .roles(user.getRole().toString())
                .build();
    }
}
