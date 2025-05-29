package com.email.authservice.service;

import java.util.Optional;

import com.email.authservice.model.User;
import com.email.authservice.respository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}